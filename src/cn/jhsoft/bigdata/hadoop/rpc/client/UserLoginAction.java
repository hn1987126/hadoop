package cn.jhsoft.bigdata.hadoop.rpc.client;

import cn.jhsoft.bigdata.hadoop.rpc.protocol.IUserLoginService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.net.InetSocketAddress;

/**
 * 客户端远程调用，指定ip+端口即可
 * Created by chen on 2017/7/12.
 */
public class UserLoginAction {

    public static void main(String[] args) throws Exception {
        IUserLoginService proxy = RPC.getProxy(IUserLoginService.class, 100L, new InetSocketAddress("localhost", 9999), new Configuration());
        String res = proxy.login("ght", "123456");
        System.out.println(res);
    }

}
