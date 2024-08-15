package cn.rentaotao.netty.chart.server;

import cn.rentaotao.netty.chart.ImMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理客户端发过来的消息
 *
 * @author rtt
 * @date 2023/2/17 16:48
 */
public class AppInboundHandler extends SimpleChannelInboundHandler<ImMessage> {

    private final MsgProcessor processor = new MsgProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        System.out.println("TerminalServerHandler");
        processor.sendMsg(ctx.channel(), msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("Socket Client: 客户端断开连接：" + cause.getMessage());
        ctx.close();
    }
}
