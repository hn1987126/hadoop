package cn.jhsoft.bigdata.hadoop.mr.flowprovince;

import cn.jhsoft.bigdata.hadoop.mr.flowsum.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

import java.util.HashMap;

/**
 * 重写 Partitioner 实现重构分区。
 * 默认的分区 是把key也就是单词进行hashCode再取模。取模是有几个Reduces就 % 几
 * 重写的话，是按省份来，有几个省份就有几个区，此类假如有5个省份。
 * <Text, FlowBean> 对应的是Map输出的 k,v 类型
 *
 * Created by chenyi9 on 2017/7/14.
 */
public class ProvincePartitioner extends Partitioner<Text, FlowBean> {

    public static HashMap<String, Integer> proviceDist = new HashMap<String, Integer>();
    static {
        proviceDist.put("136", 0);
        proviceDist.put("137", 1);
        proviceDist.put("138", 2);
        proviceDist.put("139", 3);
    }

    @Override
    public int getPartition(Text text, FlowBean flowBean, int i) {

        Integer proviceId = proviceDist.get(text.toString().substring(0, 3));
        // 如果手机号不在136,137,138,139里，则其他所有的号都在 4分区里。
        return proviceId == null ? 4 : proviceId;

    }

}
