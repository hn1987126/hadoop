package cn.jhsoft.bigdata.hadoop.mr.logenhance;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * maptask或者reducetask在最终输出时，先调用OutputFormat的getRecordWriter方法拿到一个RecordWriter
 * 然后再调用RecordWriter的write(k,v)方法将数据写出
 * 
 * @author
 * 
 */
public class LogEnhanceOutputFormat extends FileOutputFormat<Text, NullWritable> {

	@Override
	public RecordWriter<Text, NullWritable> getRecordWriter(TaskAttemptContext context) throws IOException, InterruptedException {

		FileSystem fs = FileSystem.get(context.getConfiguration());

		Path enhancePath = new Path("/wordcount/logenhance/outputhance/log.txt");
		Path tocrawlPath = new Path("/wordcount/logenhance/outputhance/url.dat");
		// 如果要访问hdfs可以这样写：
		//Path tocrawlPath = new Path("hdfs://s1:9000/wordcount/logenhance/outputhance/url.dat");

		FSDataOutputStream enhancedOs = fs.create(enhancePath);
		FSDataOutputStream tocrawlOs = fs.create(tocrawlPath);

		// 如果是要写数据库，则往此类里传mysql的相关信息或Redis的相关信息。或者直接不传，在那里面直接连数据库。
		return new EnhanceRecordWriter(enhancedOs, tocrawlOs);
	}

	/**
	 * 构造一个自己的recordwriter
	 * 
	 * @author
	 * 
	 */
	static class EnhanceRecordWriter extends RecordWriter<Text, NullWritable> {
		FSDataOutputStream enhancedOs = null;
		FSDataOutputStream tocrawlOs = null;

		public EnhanceRecordWriter(FSDataOutputStream enhancedOs, FSDataOutputStream tocrawlOs) {
			super();
			this.enhancedOs = enhancedOs;
			this.tocrawlOs = tocrawlOs;
		}

		// 这里已经可以自定义输出了，所以在这里可以直接写库，写缓存，写hdfs都可以。
		@Override
		public void write(Text key, NullWritable value) throws IOException, InterruptedException {
			String result = key.toString();
			// 如果要写出的数据是待爬的url，则写入待爬清单文件 /wordcount/logenhance/outputhance/url.txt
			if (result.contains("tocrawl")) {
				tocrawlOs.write(result.getBytes("UTF-8"));
			} else {
				// 如果要写出的数据是增强日志，则写入增强日志文件 /wordcount/logenhance/outputhance/log.txt
				enhancedOs.write(result.getBytes());
			}

		}

		@Override
		public void close(TaskAttemptContext context) throws IOException, InterruptedException {
			if (tocrawlOs != null) {
				tocrawlOs.close();
			}
			if (enhancedOs != null) {
				enhancedOs.close();
			}

		}

	}

}
