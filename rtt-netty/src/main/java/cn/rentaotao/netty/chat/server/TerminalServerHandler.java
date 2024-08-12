package cn.rentaotao.netty.chat.server;

import cn.rentaotao.netty.chat.protocol.ImMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理 Java 控制台发过来的消息
 *
 * @author rtt
 * @date 2023/2/17 16:48
 */
public class TerminalServerHandler extends SimpleChannelInboundHandler<ImMessage> {

    private final MsgProcessor processor = new MsgProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        System.out.println("TerminalServerHandler");
        processor.sendMsg(ctx.channel(), msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Socket Client: 客户端断开连接：" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
