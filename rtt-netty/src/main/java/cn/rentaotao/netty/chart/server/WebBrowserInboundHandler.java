package cn.rentaotao.netty.chart.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

/**
 * webSocket 客户端
 *
 * @author rtt
 * @date 2023/2/17 22:57
 */
public class WebBrowserInboundHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private final MsgProcessor processor = new MsgProcessor();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("WebSocketServerHandler");
        processor.sendMsg(ctx.channel(), msg.text());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String address = processor.getAddress(channel);
        System.out.println("WebSocket Client: " + address + " 异常" + cause);
        ctx.close();
    }
}
