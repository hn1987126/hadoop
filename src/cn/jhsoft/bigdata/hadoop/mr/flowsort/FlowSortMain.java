package cn.jhsoft.bigdata.hadoop.mr.flowsort;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 作业：2/在作业1（flowsum）结果的基础之上再加一个需求：将统计结果按照总流量倒序排序
 * 作业1的结果：
 * 13480253104	540	540	1080
 * 13502468823	22005	331047	353052
 * 13560436666	3348	2862	6210
 *
 * 本程序的Main函数里的输入则为 flowSum里的 /flowsum/output
 *
 * Created by chenyi9 on 2017/7/14.
 */
public class FlowSortMain {

    // Map task
    static class FlowSortMapper extends Mapper<LongWritable, Text, FlowBean, Text> {

        // 标准写法
        // FlowBean作为key，因为要根据他排序
        FlowBean bean = new FlowBean();
        // 手机号作为value
        Text v = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 按制表符切分每一行
            String[] fileds = value.toString().split("\t");
            String phone = fileds[0];
            long upFlow = Long.parseLong(fileds[1]);
            long downFlow = Long.parseLong(fileds[2]);

            bean.set(upFlow, downFlow);
            v.set(phone);

            context.write(bean, v);
        }
    }


    // Reducer task
    // FlowBean, Text, Text, FlowBean  分别指Map Task处理完，Map Tas的结构，
    // 如上一步中context.write进去的是  FlowBean作为key，手机号字符串作为value。注意FlowBean是会被序列化的
    // Text, FlowBean 是指从Reducer Task中产生的结果的k,v
    static class FlowSortReducer extends Reducer<FlowBean, Text, Text, FlowBean> {
        @Override
        protected void reduce(FlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // 因为FlowBean作为key存在的时候，每个对象序列化后即使是相同值的对象，他也是不同的序列化
            // 所以这里进来的 <FlowBean, 手机号> 都是各不相同的
            // 因此在这里只需要直接write即可，不需要加统计的逻辑。
            context.write(values.iterator().next(), key);
        }
    }

    // 主入口
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.hostname", "s1");

        Job job = Job.getInstance(conf);

        // 指定跑job的jar包
        job.setJarByClass(FlowSortMain.class);

        // 指定本业务job要使用的 Mapper/Reducer 业务类
        job.setMapperClass(FlowSortMapper.class);
        job.setReducerClass(FlowSortReducer.class);

        // 由于sort是要对所有的结果排序，因此不能指定分区数量
        // 不能作如下的设置，如果要设置也只能设置为1，默认就是1
        //job.setNumReduceTasks(5);

        // 指定Mapper输出数据的 kv 类型
        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);

        // 指定最终输出的数据的 kv 类型(也就是 Reducer )
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 指定job的输入原始文件
        FileInputFormat.setInputPaths(job, new Path("/flowsum/output2"));

        // 指定job的输出结果所在目录
        Path path = new Path("/flowsum/outsort");
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
