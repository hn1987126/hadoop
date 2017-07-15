package cn.jhsoft.bigdata.hadoop.mr.flowprovince;

import cn.jhsoft.bigdata.hadoop.mr.flowsum.FlowBean;
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
 * 此类和flowSum里一样，只是指定了自定义分区，用来把flowSum里的统计结果按省份生成不同的文件。
 *
 * 此类的目的是根据流量日志，如doc目录下的HTTP_20130313143750.log。
 * 日志里有这些数据   时间戳，手机号，串号，ip，访问网站，网站名称，上行数量，下行数量，上行流量，下行流量，网站返回码
 * 当然在集群上跑的时候，需要把这文件先放到hdfs中。此处只是说日志的格式类似那文件里的。
 * 作业：3/将统计结果按照手机归属地不同省份输出到不同文件中
 *
 * Created by chenyi9 on 2017/7/14.
 */
public class FlowCountMain {

    // Map task
    static class FlowCountMapper extends Mapper <LongWritable, Text, Text, FlowBean>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            // 按制表符切分每一行。
            String[] fileds = value.toString().split("\t");
            String phone = fileds[1];
            long upFlow = Long.parseLong(fileds[fileds.length - 3]);
            long downFlow = Long.parseLong(fileds[fileds.length - 2]);
            FlowBean flowBean = new FlowBean(upFlow, downFlow);
            context.write(new Text(phone), flowBean);

        }
    }


    // Reducer task
    static class FlowCountReducer extends Reducer <Text, FlowBean, Text, FlowBean>{
        @Override
        protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
            long upFlow = 0, downFlow = 0;
            for (FlowBean flowbean:values) {
                upFlow += flowbean.getUpFlow();
                downFlow += flowbean.getDownFlow();
            }
            FlowBean resultBean = new FlowBean(upFlow, downFlow);
            context.write(key, resultBean);
        }
    }

    // 主入口
    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        //conf.set("mapreduce.framework.name", "yarn");
        //conf.set("yarn.resourcemanager.hostname", "s1");

        Job job = Job.getInstance(conf);

        // 指定跑job的jar包
        job.setJarByClass(FlowCountMain.class);

        // 指定本业务job要使用的 Mapper/Reducer 业务类
        job.setMapperClass(FlowCountMapper.class);
        job.setReducerClass(FlowCountReducer.class);

        //###################################################################
        //###################################################################
        //###################################################################
        // 指定我们自定义的数据分区器
        job.setPartitionerClass(ProvincePartitioner.class);
        // 指定与自定义数据分区器里数据分区数量的 reducesTask，如果不指定则默认是1
        job.setNumReduceTasks(5);
        //###################################################################
        //###################################################################
        //###################################################################


        // 指定Mapper输出数据的 kv 类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        // 指定最终输出的数据的 kv 类型(也就是 Reducer )
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 指定job的输入原始文件
        FileInputFormat.setInputPaths(job, new Path("/wordcount/flowsum/input"));

        // 指定job的输出结果所在目录
        Path path = new Path("/wordcount/flowprovince/output");
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
