package cn.jhsoft.bigdata.hadoop;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by chenyi9 on 2017/7/12.
 */
public class HdfsStreamAccess {

    Configuration conf;
    FileSystem fs;


    @Before
    public void init() throws Exception {
        conf = new Configuration();
        fs = FileSystem.get(new URI("hdfs://s1:9000"), conf, "hadoop");
    }

    // 通过流的方式写文件
    @Test
    public void testUpload() throws Exception {

        FSDataOutputStream outputStream = fs.create(new Path("/ht.love"));
        FileInputStream inputStream = new FileInputStream("d:/1.txt");
        IOUtils.copy(inputStream, outputStream);
    }

    // 通过流的方式下载文件
    @Test
    public void testUpload() throws Exception {

        FSDataOutputStream outputStream = fs.create(new Path("/ht.love"));
        FileInputStream inputStream = new FileInputStream("d:/1.txt");
        IOUtils.copy(inputStream, outputStream);
    }

}
