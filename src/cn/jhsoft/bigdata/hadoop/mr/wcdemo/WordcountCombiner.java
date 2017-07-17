package cn.jhsoft.bigdata.hadoop.mr.wcdemo;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

/**
 * MapReduces中，Combiner组件的作用：
 * 在Map Task进行时，生成<单词a,数量1>，<a,1>,<a,1>,<b,1>,<b,1>这样一个一个，很费空间还有传输的时候也影响速度。
 * Combiner组件的任务是在使这样的生成，进行合并，如<a,3>,<b,2>，实质上Combiner也是继承自Reducer，此类的实现与WordcountReducer完全相同，所以可以用它来作为Combiner。
 * Created by chen on 2017/7/15.
 */
public class WordcountCombiner extends Reducer<Text, IntWritable, Text, IntWritable>
{
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

        int count = 0;
        for (IntWritable value : values){
            count += value.get();
        }
        context.write(key, new IntWritable(count));
    }
}
