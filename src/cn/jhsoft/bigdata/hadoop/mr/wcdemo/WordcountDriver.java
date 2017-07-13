package cn.jhsoft.bigdata.hadoop.mr.wcdemo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


/**
 *
 * 相当于yarn集群的客户端
 * 需要在此封装我们的 mapreduces 程序的相关运行参数，指定jar包
 * 最后提交给yarn
 *
 * Created by chen on 2017/7/14.
 */
public class WordcountDriver {


    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.hostname", "s1");

        Job job = Job.getInstance(conf);

        // 指定跑job的jar包
        job.setJarByClass(WordcountDriver.class);

        // 指定本业务job要使用的 Mapper/Reducer 业务类
        job.setMapperClass(WordcountMapper.class);
        job.setReducerClass(WordcountReducer.class);

        // 指定Mapper输出数据的 kv 类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 指定最终输出的数据的 kv 类型(也就是 Reducer )
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 指定job的输入原始文件
        FileInputFormat.setInputPaths(job, new Path("/wordcount/input"));
        // 指定job的输出结果所在目录
        FileOutputFormat.setOutputPath(job, new Path("/wordcount/output"));

        // 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean res = job.waitForCompletion(true);
        System.exit(res?0:1);

    }


}
