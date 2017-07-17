package cn.jhsoft.bigdata.hadoop.mr.flowsum;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 此类的目的是根据流量日志，如doc目录下的HTTP_20130313143750.log。
 * 日志里有这些数据   时间戳，手机号，串号，ip，访问网站，网站名称，上行数量，下行数量，上行流量，下行流量，网站返回码
 * 当然在集群上跑的时候，需要把这文件先放到hdfs中。此处只是说日志的格式类似那文件里的。
 * 作业：1/统计每一个用户（手机号）所耗费的总上行流量、下行流量，总流量
 *
 * Created by chenyi9 on 2017/7/14.
 */
public class FlowCountMain {

    // Map task
    static class FlowCountMapper extends Mapper <LongWritable, Text, Text, FlowBean>{
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            // 按制表符切分每一行
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
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.hostname", "s1");
        conf.set("fs.defaultFS", "hdfs://s1:9000/");
        System.setProperty("HADOOP_USER_NAME", "hadoop");

        Job job = Job.getInstance(conf);

        // 指定跑job的jar包
        job.setJarByClass(FlowCountMain.class);
        job.setJar("/Users/chen/java/hadoop/out/artifacts/hadoop_jar/hadoop.jar");

        // 指定本业务job要使用的 Mapper/Reducer 业务类
        job.setMapperClass(FlowCountMapper.class);
        job.setReducerClass(FlowCountReducer.class);

        // 指定Mapper输出数据的 kv 类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);

        // 指定最终输出的数据的 kv 类型(也就是 Reducer )
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);

        // 指定需要使用combiner组件，及哪个类作为combiner的逻辑，
        // combiner是为了提高效率，在Map Task的时候就进行一些合并，以至于传输给 Reduces Task 的文件不要那么大。
        // 但这也只适合能合并的情况，如果是一些运算类的，不合适合并就不要在这合并了，还是像原来一样在最后合并。
        job.setCombinerClass(FlowCountReducer.class);

        // 如果不设置InputFormat，它默认用的是TextInputFormat.class，也就是一个文件对应一个切片
        // 用如下这个设置，是在跑之前进行逻辑合并。
        job.setInputFormatClass(CombineTextInputFormat.class);
        CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);
        CombineTextInputFormat.setMinInputSplitSize(job, 2097152);

        // 指定job的输入原始文件
        FileInputFormat.setInputPaths(job, new Path("/wordcount/flowsum/input"));
        // 指定job的输出结果所在目录
        Path path = new Path("/wordcount/flowsum/output");
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
