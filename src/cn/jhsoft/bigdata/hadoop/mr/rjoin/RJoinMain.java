package cn.jhsoft.bigdata.hadoop.mr.rjoin;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 订单与产品分布在两个文件中，此程序的功能是把他们拼接起来
 *
 * Created by chen on 2017/7/15.
 */
public class RJoinMain {

    static class RJoinMapper extends Mapper<LongWritable, Text, Text, InfoBean>{

        InfoBean bean = new InfoBean();
        Text k = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            // 获得切片的文件名，看当前取的是订单文本，还是商品文本，因为他们都在同一目录底下
            FileSplit inputSplit = (FileSplit) context.getInputSplit();
            String name = inputSplit.getPath().getName();
            String pid = "";

            if (name.startsWith("order")){
                String[] fields = value.toString().split(",");
                pid = fields[2];
                bean.set(Integer.parseInt(fields[0]), fields[1], pid, Integer.parseInt(fields[3]), "", 0, 0, "");

            }else{
                String[] fields = value.toString().split(",");
                pid = fields[0];
                bean.set(0, "", pid, 0, fields[1], Integer.parseInt(fields[2]), Float.parseFloat(fields[3]), "1");

            }

            k.set(pid);
            context.write(k, bean);

        }
    }



    static class RJoinReducer extends Reducer<Text, InfoBean, InfoBean, NullWritable>{

        // 进来这里的时候，一个产品pid是key，values里存的是，一个产品Bean 和 多个订单Bean
        @Override
        protected void reduce(Text key, Iterable<InfoBean> values, Context context) throws IOException, InterruptedException {

            // 暂时订单个变量来存商品信息，后面会把他拼接到订单信息中
            InfoBean productBean = new InfoBean();
            // 用来存订单信息数组列表
            ArrayList<InfoBean> orderBeans = new ArrayList<>();

            for (InfoBean bean : values){
                // 产品
                if ("1".equals(bean.getFlag())){
                    try {
                        BeanUtils.copyProperties(productBean, bean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    // 订单
                    InfoBean orderBean = new InfoBean();
                    try {
                        BeanUtils.copyProperties(orderBean, bean);
                        orderBeans.add(orderBean);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // 合并两个Bean
            for (InfoBean b : orderBeans){
                b.setName(productBean.getName());
                b.setCategory_id(productBean.getCategory_id());
                b.setPrice(productBean.getPrice());

                context.write(b, NullWritable.get());
            }

        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        //conf.set("mapreduce.framework.name", "yarn");
        //conf.set("yarn.resourcemanager.hostname", "s1");

        Job job = Job.getInstance(conf);

        // 指定跑job的jar包
        job.setJarByClass(RJoinMain.class);

        // 指定本业务job要使用的 Mapper/Reducer 业务类
        job.setMapperClass(RJoinMapper.class);
        job.setReducerClass(RJoinReducer.class);

        // 由于sort是要对所有的结果排序，因此不能指定分区数量
        // 不能作如下的设置，如果要设置也只能设置为1，默认就是1
        //job.setNumReduceTasks(5);

        // 指定Mapper输出数据的 kv 类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(InfoBean.class);

        // 指定最终输出的数据的 kv 类型(也就是 Reducer )
        job.setOutputKeyClass(InfoBean.class);
        job.setOutputValueClass(NullWritable.class);

        // 指定job的输入原始文件
        FileInputFormat.setInputPaths(job, new Path("/wordcount/rjoin/input"));

        // 指定job的输出结果所在目录
        Path path = new Path("/wordcount/rjoin/output");
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);

        // 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean res = job.waitForCompletion(true);
        System.exit(res?0:1);
    }

}
