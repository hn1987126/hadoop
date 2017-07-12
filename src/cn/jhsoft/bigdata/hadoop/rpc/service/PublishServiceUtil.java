package cn.jhsoft.bigdata.hadoop.rpc.service;

import cn.jhsoft.bigdata.hadoop.rpc.protocol.IUserLoginService;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

/**
 * 服务器守护进程，这里发布了一个服务，指定了端口号和访问域名
 * Created by chen on 2017/7/12.
 */
public class PublishServiceUtil {


    public static void main(String[] args) throws IOException {
        RPC.Builder builder = new RPC.Builder(new Configuration());
        builder.setBindAddress("localhost")
                .setPort(9999)
                .setProtocol(IUserLoginService.class)
                .setInstance(new UserLoginServiceImpl());
        RPC.Server server = builder.build();
        server.start();

    }

}
