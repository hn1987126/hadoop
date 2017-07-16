package cn.jhsoft.bigdata.hadoop.mr.secondarysort;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * 订单按所购的商品的成交额排序
 */
public class SecondarySortMain {
	
	static class SecondarySortMapper extends Mapper<LongWritable, Text, OrderBean, NullWritable>{
		
		OrderBean bean = new OrderBean();
		
		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String line = value.toString();
			String[] fields = StringUtils.split(line, ",");
			bean.set(new Text(fields[0]), new DoubleWritable(Double.parseDouble(fields[2])));
			context.write(bean, NullWritable.get());
			
		}
		
	}
	
	static class SecondarySortReducer extends Reducer<OrderBean, NullWritable, OrderBean, NullWritable>{
		//到达reduce时，相同id的所有bean已经被看成一组，且金额最大的那个一排在第一位
		@Override
		protected void reduce(OrderBean key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
			context.write(key, NullWritable.get());
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		
		job.setJarByClass(SecondarySortMain.class);
		
		job.setMapperClass(SecondarySortMapper.class);
		job.setReducerClass(SecondarySortReducer.class);
		
		
		job.setOutputKeyClass(OrderBean.class);
		job.setOutputValueClass(NullWritable.class);

		FileInputFormat.setInputPaths(job, new Path("/wordcount/secondarysort/input"));
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path("/wordcount/secondarysort/output");
		if (fs.exists(path)){
			fs.delete(path, true);
		}
		FileOutputFormat.setOutputPath(job, path);
		
		//在此设置自定义的Groupingcomparator类，不同的bean可以根据里面的id来视为相同的bean,也就是一组
		job.setGroupingComparatorClass(ItemIdGroupingComparator.class);
		//在此设置自定义的partitioner类，用于产生分区数，相同的bean里的id在同一分区上，如果按默认的，他会所有的bean都不相同，因为是序列化的，所以那样就没办法让同一id的在同一分区。
		job.setPartitionerClass(ItemIdPartitioner.class);

		// 设置两个Reduces，那结果文件里就有几个。默认是1个
		//job.setNumReduceTasks(2);
		
		job.waitForCompletion(true);
		
	}

}
