package cn.jhsoft.zk.zkdist;

import org.apache.zookeeper.*;

/**
 * Created by chen on 2017/7/9.
 */
public class DistributedServer {

    public static final String connectString="s1:2181,s2:2181,s3:2181";
    public static final int sessionTimeout = 2000;
    ZooKeeper zkClient = null;
    private static final String parentNode = "/servers";


    /**
     * 建立连接
     * @throws Exception
     */
    public void getConnect() throws Exception {
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

    /**
     * 业务功能
     * @param hostname
     * @throws InterruptedException
     */
    public void handleBusiness(String hostname) throws InterruptedException {
        System.out.println(hostname+"start working ...");
        Thread.sleep(Long.MAX_VALUE);
    }

    /**
     * 向zk集群注册服务器信息
     * @param hostname
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void registerServer(String hostname) throws KeeperException, InterruptedException {
        String create = zkClient.create(parentNode + "/server", hostname.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println(hostname+"is online.."+create);
    }


    public static void main(String[] args) throws Exception {
        DistributedServer server = new DistributedServer();
        server.getConnect();

        // 利用zk连接 注册服务器信息
        server.registerServer(args[0]);

        // 启动业务功能
        server.handleBusiness(args[0]);
    }

}
