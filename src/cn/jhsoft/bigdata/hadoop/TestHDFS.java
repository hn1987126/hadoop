package cn.jhsoft.bigdata.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by chenyi9 on 2017/7/12.
 */
public class TestHDFS {

    Configuration conf;
    FileSystem fs;


    @Before
    public void init() throws Exception {
        conf = new Configuration();
        fs = FileSystem.get(new URI("hdfs://s1:9000"), conf, "hadoop");
    }

    @Test
    public void testUpload() throws Exception {

        fs.copyFromLocalFile(new Path("d:/testUpload.html"), new Path("/"));
        fs.close();

    }

    @Test
    public void testDownload() throws Exception {

        fs.copyToLocalFile(false, new Path("/1.txt"), new Path("d:/"), true);
        fs.close();

    }

    @Test
    public void testConf(){
        Iterator<Map.Entry<String, String>> iterator = conf.iterator();
        while (iterator.hasNext()){
            Map.Entry entry = iterator.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    @Test
    public void testMkdir() throws Exception {
        boolean mkdirs = fs.mkdirs(new Path("/abc/b/c"));
        System.out.println(mkdirs);
        fs.close();
    }

    @Test
    public void testDel() throws Exception {
        boolean mkdirs = fs.delete(new Path("/abc"), true);
        System.out.println(mkdirs);
        fs.close();
    }

    // 递归所有子目录，只得到文件
    @Test
    public void testList() throws Exception {
        RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path("/"), true);
        while (files.hasNext()){
            LocatedFileStatus lfs = files.next();
            System.out.println(lfs.getPath().getName());
            // 取块信息
            BlockLocation[] blockLocations = lfs.getBlockLocations();
            for (BlockLocation b : blockLocations){
                System.out.println("块名称："+ Arrays.toString(b.getNames()));
                System.out.println("块起始偏移量："+b.getOffset());
                System.out.println("块长度："+b.getLength());
                System.out.println("块(datanode)所在服务器："+ Arrays.toString(b.getHosts()));
            }

        }
        fs.close();
    }

    // 当前目录下的文件和目录  列表
    @Test
    public void testListSinge() throws Exception {
        FileStatus[] files = fs.listStatus(new Path("/"));
        for (FileStatus fst:files) {
            System.out.print(fst.getPath().getName());
            System.out.println(fst.isFile()?"--file":"--dir");
        }
        fs.close();
    }

}
