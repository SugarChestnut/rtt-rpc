package cn.rentaotao.netty.protocol.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

/**
 * @author rtt
 * @create 2021/3/24 10:41
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 第一个 handler 传递进来的 msg 是 ByteBuf 类型，后的是前一个处理后传递的类型
        ByteBuf bf = (ByteBuf) msg;
        // 获取可读取的数组长度
        int len = bf.readableBytes();
        // 创建数组
        byte[] bt = new byte[len];
        /*
            使用 getBytes() 可以不改变原 ByteBuf 的索引信息
         */
        bf.getBytes(0, bt);
        System.out.println("客户端接收消息: " + new String(bt, StandardCharsets.UTF_8));
        // 释放 ByteBuf 的两种方法，两种方法不能同时使用
        // 方法一：手动释放
        bf.release();
        // 方法二：在流水线末尾，有一个自动释放的处理器
//        super.channelRead(ctx, msg);
    }
}
