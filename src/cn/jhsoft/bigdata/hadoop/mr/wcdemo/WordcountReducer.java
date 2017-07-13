package cn.jhsoft.bigdata.hadoop.mr.wcdemo;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 *
 * extends Reducer 泛型，四个参数的意思：
 * 第一个,第二个 KEYIN,VALUEIN   分别对应mapper输出的 KEYOUT,VALUEOUT
 * 第三个,第四个 KEYOUT,VALUEOUT  是自定义reducer逻辑处理结果的输出数据类型
 * KEYOUT  在本类中是单词
 * VALUEOUT  在本类中是总次数
 *
 *
 * Created by chen on 2017/7/13.
 */
public class WordcountReducer extends Reducer <Text, IntWritable, Text, IntWritable>{


    /**
     *
     * 入参key,是一组相同单词kv对的key,如：
     * <ght,1><love,1><hi,1><ght,1><you,1>
     *
     */
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
        int count = 0;

        for (IntWritable value:values) {
            count += value.get();
        }

        context.write(key, new IntWritable(count));

    }
}
