package cn.rentaotao.netty.example.encode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author rtt
 * @create 2021/3/25 09:16
 */
public class String2IntegerEncoder extends MessageToMessageEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, String s, List<Object> list) throws Exception {
        char[] chars = s.toCharArray();
        for (char c : chars) {
            if (c >= '0' && c <= '9') {
                list.add(new Integer("" + c));
            }
        }
    }
}
