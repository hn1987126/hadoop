package cn.jhsoft.bigdata.hadoop.mr.index;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 根据IndexOneMain的结果：
 * ght--a.txt  2
 * ght--b.txt   1
 * tom--a.txt   1
 * tom--c.txt   1
 *
 *
 * 想得到：
 * ght a.txt 2,b.txt 1
 * tom a.txt 1,c.txt 1
 *
 *
 *
 * Created by chen on 2017/7/16.
 */
public class IndexTwoMain {

    static class IndexTwoMapper extends Mapper<LongWritable, Text, Text, Text> {

        Text k = new Text();
        Text v = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String fields[] = value.toString().split("--");
            k.set(fields[0]);
            v.set(fields[1]);
            context.write(k, v);
        }
    }

    static class IndexTwoReducer extends Reducer<Text, Text, Text, Text> {

        Text v = new Text();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

            StringBuffer sb = new StringBuffer();
            for (Text c:values){
                sb.append(c.toString()+",");
            }
            String s = sb.toString();
            if (!"".equals(s)) {
                s = s.replace("\t", " ");
                s = s.substring(0, sb.length() - 1);
            }

            v.set(s);
            context.write(key, v);

        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(IndexTwoMain.class);
        job.setMapperClass(IndexTwoMapper.class);
        job.setReducerClass(IndexTwoReducer.class);

        // 如果最终输出的跟maptask的输出是一样的，则不需要再设置Map的输出key和value
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path("/wordcount/indexone/output"));
        // 指定job的输出结果所在目录
        Path path = new Path("/wordcount/indexone/tow.output");
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);

        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }

}
