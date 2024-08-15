package cn.rentaotao.netty.im.client.handler;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author rtt
 * @create 2021/3/31 20:07
 */
public class ChatMsgHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 响应为空
        if (!(msg instanceof ImOuterClass.Im.Message)) {
            // 传递
            super.channelRead(ctx, msg);
            return;
        }

        // 获取响应类型
        ImOuterClass.Im.Message message = (ImOuterClass.Im.Message) msg;
        ImOuterClass.Im.HeadType headType = message.getHeadType();

        // 响应类型非登录响应
        if (!ImOuterClass.Im.HeadType.MESSAGE_REQUEST.equals(headType)) {
            // 传递
            super.channelRead(ctx, msg);
            return;
        }
        ImOuterClass.Im.MessageRequest messageRequest = message.getMessageRequest();

        System.out.println("-----收到消息-----");
        System.out.println("from:" + messageRequest.getForm());
        System.out.println("content:" + messageRequest.getContent());
        System.out.println("time:" + messageRequest.getTime());
    }
}
