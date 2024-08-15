package cn.rentaotao.netty.protocol.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * ChannelHandler.Sharable 表示该 handler 可以被多个流水线持有，如果在 handler 中定义共享字段，就会有线程安全问题
 * 默认不支持 handler 添加到多个 pipeline
 *
 * @author rtt
 * @create 2021/3/21 09:33
 */

@ChannelHandler.Sharable
public class NettyDiscardHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf bf = (ByteBuf) msg;
        System.out.println("msg type: " + (bf.hasArray() ? "堆内存" : "堆外内存"));
        // 默认分配为堆外内存
        int len = bf.readableBytes();
        byte[] bt = new byte[len];
        bf.getBytes(0, bt);
        System.out.println("消息: " + new String(bt, StandardCharsets.UTF_8));
        System.out.println("写回前, msg.refCnt: " + bf.refCnt());

//        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
//        buffer.writeBytes("服务器的回显消息".getBytes());

        // 在写回数据后，会将 buffer 释放
        ChannelFuture channelFuture = ctx.writeAndFlush(msg);

        channelFuture.addListener(channelFutureListener -> System.out.println("写回后, msg.refCnt: " + bf.refCnt()));

    }
}
