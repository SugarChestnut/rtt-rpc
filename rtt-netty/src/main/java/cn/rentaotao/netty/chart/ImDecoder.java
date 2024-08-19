package cn.rentaotao.netty.chart;

import cn.rentaotao.common.utils.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author rtt
 * @date 2023/2/17 14:55
 */
public class ImDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("ImDecoder");
        try {
            int len = in.readableBytes();
            byte[] array = new byte[len];
            in.getBytes(in.readerIndex(), array, 0, len);
            ImMessage msg = JsonUtils.string2Object(new String(array, StandardCharsets.UTF_8), ImMessage.class);

            if (msg != null && !Imp.isImp(msg.getCmd())) {
                ctx.channel().pipeline().remove(this);
                return;
            }

            out.add(msg);
            in.release();
        } catch (Exception e) {
            // 每个链接都有一个自己的 pipeline，说明当前不是 tcp 连接，移除当前解码器
            System.out.println("Remove ImDecoder");
            ctx.channel().pipeline().remove(this);
        }
    }
}
