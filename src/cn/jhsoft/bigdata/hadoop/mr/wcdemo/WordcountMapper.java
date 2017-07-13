package cn.jhsoft.bigdata.hadoop.mr.wcdemo;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 *
 * extends Mapper 泛型，四个参数的意思：
 * 第一个  KEYIN(定义的参数名是叫这个名)  Mapper里的map是一次取一行，这个参数是代表偏移量，如当前处理到了第多少行，
 * 第二个  VALUEIN，  所取到的那一行的文本内容  String也就是Text类型
 * 第三个  KEYOUT,    map处理完成后，输出数据中的key，在本类中是指单词，String也就是Text类型
 * 第四个  VALUEOUT,   map处理完成后输出到数据中的value，本类中是指单词的次数，IntWritable类型
 *
 * Created by chen on 2017/7/13.
 */
public class WordcountMapper extends Mapper <LongWritable, Text, Text, IntWritable>{

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        // 将maptask传来的那行文本内容先转换成String
        String line = value.toString();
        // 把一行用空隔分隔成多个单词
        String[] words = line.split(" ");
        for (String word:words) {
            // 单词作为key，将次数1作为value，便于后续数据分发，可根据单词分发，便于相同单词汇总到相同的reduce task
            context.write(new Text(word), new IntWritable(1));
        }
    }
}
