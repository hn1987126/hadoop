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

        // 这是在远程跑
        //conf.set("mapreduce.framework.name", "yarn");
        //conf.set("yarn.resourcemanager.hostname", "s1");
        //conf.set("fs.defaultFS", "hdfs://s1:9000/");
        System.setProperty("HADOOP_USER_NAME", "hadoop");

        // 这是跑本地的本在跑的底下这两行不配，默认就是这样的。。。
        //conf.set("mapreduce.framework.name", "local");
        //conf.set("fs.defaultFS", "file:///");

        Job job = Job.getInstance(conf);

        // 指定跑job的jar包,这种只合适在集群上用 hadoop jar xx.jar 主类名   这样来执行，
        //job.setJarByClass(WordcountDriver.class);
        // 但是如果是想在服务器上用 java -jar xxx 这样执行，得这里指定jar名
        job.setJar("/home/hadoop/hadoop.jar");
        // 本地需要这样指定
        job.setJar("D:\\Java\\hadoop\\out\\artifacts\\hadoop_jar\\hadoop.jar");

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
        //FileInputFormat.setInputPaths(job, new Path("/wordcount/input"));
        // 指定job的输出结果所在目录
        //FileOutputFormat.setOutputPath(job, new Path("/wordcount/output3"));
        FileInputFormat.setInputPaths(job, new Path("D:\\me\\input"));
        FileOutputFormat.setOutputPath(job, new Path("D:\\me\\output"));

        // 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean res = job.waitForCompletion(true);
        System.exit(res?0:1);

    }


}
