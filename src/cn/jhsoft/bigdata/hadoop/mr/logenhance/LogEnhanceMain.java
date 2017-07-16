package cn.jhsoft.bigdata.hadoop.mr.logenhance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class LogEnhanceMain {

	static class LogEnhanceMapper extends Mapper<LongWritable, Text, Text, NullWritable> {

		Map<String, String> ruleMap = new HashMap<String, String>();

		Text k = new Text();
		NullWritable v = NullWritable.get();

		// 从数据库中加载规则信息倒ruleMap中
		@Override
		protected void setup(Context context) throws IOException, InterruptedException {

			try {
				DBLoader.dbLoader(ruleMap);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			// 全局计数器，所有MapReduces机器上全局记录
			// 获取一个计数器用来记录不合法的日志行数, 组名, 计数器名称
			Counter counter = context.getCounter("malformed", "malformedline");


			String line = value.toString();
			String[] fields = StringUtils.split(line, "\t");
			try {
				String url = fields[0];
				String content_tag = ruleMap.get(url);
				// 判断内容标签是否为空，如果为空，则只输出url到待爬清单；如果有值，则输出到增强日志
				if (content_tag == null) {
					k.set(url + "\t" + "tocrawl" + "\n");
					context.write(k, v);
				} else {
					k.set(line + "\t" + content_tag + "\n");
					context.write(k, v);
				}

			} catch (Exception exception) {
				// 计数器加1
				counter.increment(1);
			}
		}

	}

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		Job job = Job.getInstance(conf);

		job.setJarByClass(LogEnhanceMain.class);

		job.setMapperClass(LogEnhanceMapper.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);

		// ################   ****** 这是核心 *******
		// ################
		// 要控制不同的内容写往不同的目标路径，可以采用自定义outputformat的方法
		job.setOutputFormatClass(LogEnhanceOutputFormat.class);
		// ################
		// ################


		FileInputFormat.setInputPaths(job, new Path("/wordcount/logenhance/input"));

		// 尽管我们用的是自定义outputformat，但是它是继承制fileoutputformat
		// 在fileoutputformat中，必须输出一个_success文件，所以在此还需要设置输出path
		FileSystem fs = FileSystem.get(conf);
		Path path = new Path("/wordcount/logenhance/output");
		if (fs.exists(path)){
			fs.delete(path, true);
		}
		FileOutputFormat.setOutputPath(job, path);

		// 不需要reducer
		job.setNumReduceTasks(0);

		job.waitForCompletion(true);
		System.exit(0);

	}

}
