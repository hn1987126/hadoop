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
import java.util.Arrays;

/**
 * 第二步：
 * 根据上一步的结果：A是哪些人的好友，B是哪些人的好友
 *
 A	C,F,B,D,
 B	A,F,E,
 C	B,F,E,A,
 D	C,F,E,A,
 E	D,A,F,B,
 F	C,A,D,
 *
 * 把A的好友们，两两配对作为key，本人作为value，
 * 在ReducesTask里，相同的key，他们的value拼接起来就是他们共同的好友。
 *
 * Created by chen on 2017/7/16.
 */
public class SharedFriendsStepTwoMain {

    static class SharedFriendsStepTwoMapper extends Mapper<LongWritable, Text, Text, Text>{

        Text k = new Text();
        Text v = new Text();

        // 输入 行内容： A	C,F,B,D,
        // 输出 <B-C,A>,<C-D,A>
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] fields = value.toString().split("\t");
            String owner = fields[0];
            String[] persons = fields[1].split(",");

            // 把persons排序下，以统一A,B这种，怕出现B-A这种。
            Arrays.sort(persons);

            v.set(owner);

            for (int i=0; i<persons.length-1; i++){
                for (int j=i+1; j<persons.length; j++){
                    k.set(persons[i]+"-"+persons[j]);

                    // 输出 <好友-好友，主人>
                    context.write(k, v);
                }
            }
        }
    }


    static class SharedFriendsStepTwoReducer extends Reducer<Text, Text, Text, Text>{

        Text v = new Text();

        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            StringBuffer sb = new StringBuffer();
            for(Text t:values){
                sb.append(t.toString()).append(",");
            }

            v.set(sb.toString());
            context.write(key, v);
        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(SharedFriendsStepTwoMain.class);
        job.setMapperClass(SharedFriendsStepTwoMapper.class);
        job.setReducerClass(SharedFriendsStepTwoReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.setInputPaths(job, new Path("/wordcount/fans/output"));
        FileSystem fs = FileSystem.get(conf);
        Path path = new Path("/wordcount/fans/setp2.output");
        if (fs.exists(path)){
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);

        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }

}
