package cn.rentaotao.netty.chat.protocol;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author rtt
 * @date 2023/2/17 14:55
 */
public class ImDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            int len = in.readableBytes();
            byte[] array = new byte[len];
            in.getBytes(in.readerIndex(), array, 0, len);
            ImMessage msg = JSON.parseObject(array, ImMessage.class);

            if (msg != null && !Imp.isImp(msg.getCmd())) {
                ctx.channel().pipeline().remove(this);
                return;
            }

            out.add(JSON.parseObject(array, ImMessage.class));
            in.clear();
        } catch (Exception e) {
            ctx.channel().pipeline().remove(this);
        }
    }
}
