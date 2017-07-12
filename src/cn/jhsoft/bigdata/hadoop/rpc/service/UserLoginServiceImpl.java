package cn.jhsoft.bigdata.hadoop.rpc.service;

import cn.jhsoft.bigdata.hadoop.rpc.protocol.IUserLoginService;

/**
 * 服务器端实现类，接收客户远程调用并返回结果，实现协议
 * Created by chen on 2017/7/12.
 */
public class UserLoginServiceImpl implements IUserLoginService{
    @Override
    public String login(String name, String passwd) {
        return name + "登录成功！";
    }
}
