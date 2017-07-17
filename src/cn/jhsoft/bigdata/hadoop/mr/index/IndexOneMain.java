package cn.jhsoft.bigdata.hadoop.mr.index;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 需求：
 * a.txt中有单词：ght tom lucy cat scp ght
 * b.txt中有单词：ght love lucy
 * c.txt中有单词：love ght tom
 *
 * 想得到如下的结果
 * ght--a.txt  2
 * ght--b.txt   1
 * tom--a.txt   1
 * tom--c.txt   1
 *
 *
 * Created by chen on 2017/7/16.
 */
public class IndexOneMain {

    static class IndexOneMapper extends Mapper<LongWritable, Text, Text, IntWritable>{

        Text k = new Text();
        IntWritable v = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String fields[] = value.toString().split(" ");

            // 文件名
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            String fileName = fileSplit.getPath().getName();

            for (String word:fields){
                k.set(word+"--"+fileName);
                context.write(k, v);
            }
        }
    }

    static class IndexOneReducer extends Reducer<Text, IntWritable, Text, IntWritable>{

        IntWritable v = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

            int count = 0;
            for (IntWritable c:values){
                count += c.get();
            }

            v.set(count);
            context.write(key, v);

        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(IndexOneMain.class);
        job.setMapperClass(IndexOneMapper.class);
        job.setReducerClass(IndexOneReducer.class);

        // 如果最终输出的跟maptask的输出是一样的，则不需要再设置Map的输出key和value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        // 这两句可以省掉
        //job.setMapOutputKeyClass(Text.class);
        //job.setMapOutputValueClass(IntWritable.class);

        FileInputFormat.setInputPaths(job, new Path("/wordcount/indexone/input"));
        // 指定job的输出结果所在目录
        Path path = new Path("/wordcount/indexone/output");
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);

        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }

}
