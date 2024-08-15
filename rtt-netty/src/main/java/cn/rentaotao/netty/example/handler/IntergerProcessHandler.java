package cn.rentaotao.netty.example.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author rtt
 * @create 2021/3/24 14:13
 */
public class IntergerProcessHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Integer i = (Integer) msg;
        System.out.println("处理数据: " + i);
    }
}
