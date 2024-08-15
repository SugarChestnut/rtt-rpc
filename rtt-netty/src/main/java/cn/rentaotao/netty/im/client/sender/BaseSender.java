package cn.rentaotao.netty.im.client.sender;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.client.ClientSession;
import io.netty.channel.ChannelFuture;

/**
 * @author rtt
 * @create 2021/3/29 10:38
 */
public abstract class BaseSender {

    ClientSession session;

    BaseSender(ClientSession session) {
        this.session = session;
    }

    /**
     *
     * 调用连接通道发送信息
     *
     * @param msg 信息
     */
    protected void sendMsg(ImOuterClass.Im.Message msg) {
        if (null == session || !session.isConnected()) {
            System.out.println("连接还没有成功");
        }

        ChannelFuture channelFuture = session.writeAndFlush(msg);

        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                System.out.println("消息发送成功");
            } else {
                System.out.println("消息发送失败");
            }
        });
    }

    /**
     * 通过该方法发送信息
     */
    public abstract void send(ImOuterClass.Im.Message msg);
}
