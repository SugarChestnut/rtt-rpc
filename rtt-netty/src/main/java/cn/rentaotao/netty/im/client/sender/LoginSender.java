package cn.rentaotao.netty.im.client.sender;


import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.client.ClientSession;

/**
 * @author rtt
 * @create 2021/3/30 10:09
 */
public class LoginSender extends BaseSender{

    public LoginSender(ClientSession session) {
        super(session);
    }

    /**
     * 通过登录信息构建类构建登录信息，再发送登录信息
     */
    @Override
    public void send(ImOuterClass.Im.Message msg) {
        System.out.println("客户端登录……");
        sendMsg(msg);
    }
}
