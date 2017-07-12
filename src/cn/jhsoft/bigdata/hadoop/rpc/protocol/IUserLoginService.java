package cn.jhsoft.bigdata.hadoop.rpc.protocol;

/**
 * 协议，服务器和客户端都需要用
 * Created by chen on 2017/7/12.
 */
public interface IUserLoginService {

    public static final long versionID = 100L;
    public String login(String name, String passwd);

}
