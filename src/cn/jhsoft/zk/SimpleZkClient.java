package cn.jhsoft.zk;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;

import java.util.List;


/**
 * Created by chen on 2017/7/9.
 */
public class SimpleZkClient {

    public static final String connectString="s1:2181,s2:2181,s3:2181";
    public static final int sessionTimeout = 2000;
    ZooKeeper zkClient = null;

    @Before
    public void init() throws Exception {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 收到事件通知后的回调函数
                System.out.println(event.getType() + "---" + event.getPath());
                try {
                    zkClient.getChildren("/", true);
                } catch (Exception e) {
                }
            }
        });
    }

    @Test
    public void testCreated() throws Exception {
        String nodeCreated = zkClient.create("/idea", "hi".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Test
    public void getChildren() throws KeeperException, InterruptedException {
        List<String> list = zkClient.getChildren("/", true);
        for (String str:list) {
            System.out.println(str);
        }
        Thread.sleep(Long.MAX_VALUE);
    }

    @Test
    public void testNodeExist() throws KeeperException, InterruptedException {
        Stat stat = zkClient.exists("/idea", false);
        System.out.println(stat == null?"not exist":"exist");
    }

    @Test
    public void getData() throws KeeperException, InterruptedException {
        byte[] data = zkClient.getData("/idea", false, null);
        System.out.println(new String(data));
    }

    @Test
    public void editData() throws KeeperException, InterruptedException {
        zkClient.setData("/idea", "hihi".getBytes(), -1);
        getData();
    }

    @Test
    public void delete() throws KeeperException, InterruptedException {
        zkClient.delete("/idea", -1);
        testNodeExist();
    }

}
