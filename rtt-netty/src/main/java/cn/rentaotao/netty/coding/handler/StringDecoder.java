package cn.rentaotao.netty.coding.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author rtt
 * @create 2021/3/24 16:37
 */
public class StringDecoder extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf bf = (ByteBuf) msg;
        String str = new String(bf.array(), bf.arrayOffset() + bf.readerIndex(), bf.readableBytes());
        super.channelRead(ctx, str);
    }
}