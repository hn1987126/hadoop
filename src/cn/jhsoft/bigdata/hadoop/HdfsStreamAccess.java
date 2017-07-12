package cn.jhsoft.bigdata.hadoop;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Before;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;

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
    public void testDownload() throws Exception {

        FSDataInputStream inputStream = fs.open(new Path("/ht.love"));
        FileOutputStream outputStream = new FileOutputStream("d:/2.txt");
        IOUtils.copy(inputStream, outputStream);
    }

    // 通过流的方式 读取文件,直接就输入了，不用println
    @Test
    public void testCat() throws Exception {

        FSDataInputStream inputStream = fs.open(new Path("/ht.love"));
        IOUtils.copy(inputStream, System.out);
    }

    // 通过流的方式下载文件 分片来读,如只读取2个长度
    @Test
    public void testRandomAccess() throws Exception {

        FSDataInputStream inputStream = fs.open(new Path("/ht.love"));
        FileOutputStream outputStream = new FileOutputStream("d:/2.txt.1");
        IOUtils.copyLarge(inputStream, outputStream, 0, 2);
    }



}
