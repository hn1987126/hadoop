package cn.jhsoft.bigdata.hadoop.mr.wcdemo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.CombineTextInputFormat;
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

        // 这是在远程跑,其实如果打包到服务器上去用hadoop jar执行，以下这几行远程的也不用写，因为服务器上有相关的配置。
        // ##############
        // 想本地跑远程集群，用远程的yarn来跑Map Reduces，需要下面这四行
        // ##############
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.hostname", "s1");
        conf.set("fs.defaultFS", "hdfs://s1:9000/");
        System.setProperty("HADOOP_USER_NAME", "hadoop");

        // 这是跑本地的本在跑的底下这两行不配，默认就是这样的。。。
        //conf.set("mapreduce.framework.name", "local");
        //conf.set("fs.defaultFS", "file:///");

        Job job = Job.getInstance(conf);

        // 指定跑job的jar包,这种只合适在集群上用 hadoop jar xx.jar 主类名   这样来执行，或在本地的yarn来执行
        job.setJarByClass(WordcountDriver.class);
        // 但是如果是想在服务器上用 java -jar xxx 这样执行，得这里指定jar名
        //job.setJar("/home/hadoop/hadoop.jar");

        // 本地执行 输入是集群上的数据，输出是服务器上的数据，用服务器的yarn来运算。需要指定下面的jar
        // ##############
        // 想本地跑远程集群，用远程的yarn来跑Map Reduces，需要下面这四行
        // ##############
        job.setJar("/Users/chen/java/hadoop/out/artifacts/hadoop_jar/hadoop.jar");

        // 指定本业务job要使用的 Mapper/Reducer 业务类
        job.setMapperClass(WordcountMapper.class);
        job.setReducerClass(WordcountReducer.class);

        // 指定Mapper输出数据的 kv 类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        // 指定最终输出的数据的 kv 类型(也就是 Reducer )
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // 指定需要使用combiner组件，及哪个类作为combiner的逻辑，
        // combiner是为了提高效率，在Map Task的时候就进行一些合并，以至于传输给 Reduces Task 的文件不要那么大。
        // 但这也只适合能合并的情况，如果是一些运算类的，不合适合并就不要在这合并了，还是像原来一样在最后合并。
        job.setCombinerClass(WordcountCombiner.class);
        // 或者不需要上面的那个类，直接用Reducer类即可。
        // job.setCombinerClass(WordcountReducer.class);

        // 如果不设置InputFormat，它默认用的是TextInputFormat.class，也就是一个文件对应一个切片
        // 用如下这个设置，是在跑之前进行逻辑合并。
        job.setInputFormatClass(CombineTextInputFormat.class);
        CombineTextInputFormat.setMaxInputSplitSize(job, 4194304);
        CombineTextInputFormat.setMinInputSplitSize(job, 2097152);

        // 指定job的输入原始文件
        FileInputFormat.setInputPaths(job, new Path("/wordcount/input"));
        // 指定job的输出结果所在目录
        Path path = new Path("/wordcount/output");
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);
        //FileInputFormat.setInputPaths(job, new Path("D:\\me\\input"));
        //FileOutputFormat.setOutputPath(job, new Path("D:\\me\\output"));

        // 将job中配置的相关参数，以及job所用的java类所在的jar包，提交给yarn去运行
        boolean res = job.waitForCompletion(true);
        System.exit(res?0:1);

    }


}
