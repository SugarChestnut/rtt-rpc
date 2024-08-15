package cn.rentaotao.netty.im.client.sender;


import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.client.ClientSession;

/**
 * @author rtt
 * @create 2021/3/30 17:04
 */
public class ChatSender extends BaseSender{

    public ChatSender(ClientSession session) {
        super(session);
    }

    @Override
    public void send(ImOuterClass.Im.Message message) {
        if (!session.isLogin()) {
            System.out.println("当前没有登录");
            return;
        }
        System.out.println("发送信息……");
        sendMsg(message);
    }
}
