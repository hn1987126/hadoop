package cn.jhsoft.zk.zkdist;

import org.apache.zookeeper.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen on 2017/7/9.
 */
public class DistributedClient {

    public static final String connectString="s1:2181,s2:2181,s3:2181";
    public static final int sessionTimeout = 2000;
    ZooKeeper zkClient = null;
    private static final String parentNode = "/servers";
    // volatile是指所有的更改和读取 都是从主线程那取
    private volatile List<String> serverList;

    /**
     * 建立连接
     * @throws Exception
     */
    public void getConnect() throws Exception {
        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 收到事件通知后的回调函数
                try {
                    getServerList();
                } catch (Exception e) {
                }
            }
        });
    }

    /**
     * 业务功能
     * @throws InterruptedException
     */
    public void handleBusiness() throws InterruptedException {
        System.out.println("client working ...");
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 获取 服务器列表
     * @throws Exception
     */
    public void getServerList() throws Exception{
        List<String> children = zkClient.getChildren(parentNode, true);
        List<String> servers = new ArrayList<String>();
        for (String child:children) {
            byte[] data = zkClient.getData(parentNode + "/" + child, false, null);
            servers.add(new String(data));
        }
        serverList = servers;
        System.out.println(serverList);
    }


    public static void main(String[] args) throws Exception {
        DistributedClient client = new DistributedClient();
        client.getConnect();

        // 获取servers的子节点信息(并监听)，从中获取服务器信息列表
        client.getServerList();

        // 启动业务功能
        client.handleBusiness();
    }

}
