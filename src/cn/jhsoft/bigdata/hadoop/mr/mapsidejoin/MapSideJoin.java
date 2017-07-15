package cn.jhsoft.bigdata.hadoop.mr.mapsidejoin;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 通过DistributedCache[分布式的缓存]来解决数据倾斜问题
 * 省去Reduces Task，通过Map Task就解决了问题  （job.setNumReduceTasks(0)）
 * Map Reduces会给每个Map Task一个公共的缓存的文件。
 *
 * Created by chen on 2017/7/15.
 */
public class MapSideJoin {


    static class MapSideJoinMapper extends Mapper<LongWritable, Text, Text, NullWritable>{

        // 产品字典，用来存放所有产品信息
        Map<String, String> productInfoMap = new HashMap<String, String>();
        Text t = new Text();

        /**
         * setup方法是在maptask处理数据之前调用一次
         * 此方法用于加载所有产品信息Map
         * 可以做一些初始化的工作。
         * setup方法是在调map方法之前会调一次，然后就是循环map方法。
         * 在Main方法里启动job已经设置了要把公共文件放在每个task的目录下，因此可以读文件操作
         *
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void setup(Context context) throws IOException, InterruptedException {

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("product.txt")));
            String line;
            while (StringUtils.isNotEmpty(line = br.readLine())){
                String[] fields = line.split(",");
                productInfoMap.put(fields[0], fields[1]);
            }
            br.close();
        }

        // 由于已经持有完整的产品信息表，所以在map方法中就能实现join逻辑了
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] fields = value.toString().split(",");
            String productName = productInfoMap.get(fields[2]);
            // 追加在现有的行上
            t.set(value.toString() + "\t" + productName);

            context.write(t, NullWritable.get());

        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf);

        job.setJarByClass(MapSideJoin.class);
        job.setMapperClass(MapSideJoinMapper.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        FileInputFormat.setInputPaths(job, new Path("/wordcount/mapsidejoin/input"));
        // 指定job的输出结果所在目录
        Path path = new Path("/wordcount/mapsidejoin/output");
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(path)){
            fs.delete(path, true);
        }
        FileOutputFormat.setOutputPath(job, path);

        //####################
        // 指定需要缓存一个文件到所有的map task运行节点工作目录
        //####################
//        job.addArchiveToClassPath(archive);   // 缓存jar包到task运行节点的classpath中
//        job.addFileToClassPath(file);   //缓存普通文件到task运行节点的classpath中
//        job.addCacheArchive(uri);   //缓存压缩包文件到task运行节点的工作目录
        job.addCacheFile(new URI("file:/wordcount/mapsidejoin/product.txt"));   //缓存普通文件到task运行节点的工作目录

        // ################
        // map端join的逻辑不需要reduce阶段，设置reducetask的数量为0
        // ################
        job.setNumReduceTasks(0);

        boolean res = job.waitForCompletion(true);
        System.exit(res ? 0 : 1);
    }


}
