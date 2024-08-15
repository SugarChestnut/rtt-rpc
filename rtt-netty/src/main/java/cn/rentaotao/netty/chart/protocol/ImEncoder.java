package cn.rentaotao.netty.chart.protocol;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author rtt
 * @date 2023/2/17 16:00
 */
public class ImEncoder extends MessageToByteEncoder<ImMessage> {


    @Override
    protected void encode(ChannelHandlerContext ctx, ImMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(JSON.toJSONBytes(msg));
    }
}
