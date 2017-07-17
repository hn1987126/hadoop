package cn.jhsoft.bigdata.hadoop.mr.fans;

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
 * 要实现的需求：
 * 以下是qq的好友关系，如A的好友有 BCDEF
 * 求出哪些人两两之间有共同好友，及他两的共同好友都有谁？
 *
 * A:B,C,D,E,F
 * B:A,C,E
 * C:F,A,D
 * D:A,E,F
 * E:B,C,D
 * F:A,B,C,D,E
 *
 *
 * 解题思路：
 * 第一步（本程序）：求 A是哪些人的好友，即A在哪些人的冒号后面，得出
 *     A--B,C,D,F
 *     B--A,E,F
 *
 *     第二步(下一个程序实现)：A是哪些人的好友，把这些人两两配对作为k，
 *     这样的话，就可以是到这样的kv:[B-C 共同好友A]
 *     B-C A
 *     B-D A
 *     B-F A
 *     C-D A
 *     C-F A
 *     D-F A
 *
 *     A-E B
 *     A-F B
 *     E-F B
 *
 *     相同的Key，他们的value拼接就是他们共同的好友。
 *
 *
 *
 *
 * Created by chen on 2017/7/16.
 */
public class SharedFriendsStepOneMain {

    static class SharedFriendsStepOneMapper extends Mapper<LongWritable, Text, Text, Text>{

        Text k = new Text();
        Text v = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            // 输入是：A:B,C,D,E,F，这样的行

            String[] fields = value.toString().split(":");
            String person = fields[0];
            String[] friends = fields[1].split(",");
            for (String f:friends){
                // 输出<好友，主人>,意思是要知道B是A的好友，C是A的好友，B是E的好友，等这样一堆的k,v,目的是想知道某个人，是哪些人的好友。
                // 如 <B,A><C,A>,<D,A><E,A><F,A><A,B><C,B><E,B><F,C>,<A,C>xxx
                // 这里在Reduce的时候，就会有<E,A>和<E,B>一块处理,还有<F,A><F,c>等。
                // Reduce的时候就会得到E:A,B   F:A,C
                k.set(f);
                v.set(person);
                context.write(k, v);
            }
        }
    }

    static class SharedFriendsStepOneReducer extends Reducer<Text, Text, Text, Text>{

        Text v = new Text();

        /**
         * 进来的是  <B,A><C,A>,<D,A><E,A><F,A><A,B><C,B><E,B><F,C>,<A,C>  的结果：<E,[A,B]><F,[A,C]>
         * 输出的是：<E,   A,B><F   A,C>
         *
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuffer sb = new StringBuffer();
            for (Text t:values){
                sb.append(t.toString()+",");
            }
            v.set(sb.toString());
            context.write(key, v);
        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(SharedFriendsStepOneMain.class);
        job.setMapperClass(SharedFriendsStepOneMapper.class);
        job.setReducerClass(SharedFriendsStepOneReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path("/wordcount/fans/input"));
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path("/wordcount/fans/output");
        if (fs.exists(path)){
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);

        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }

}
