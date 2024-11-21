package cn.rentaotao.netty.coding.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author rtt
 * @create 2021/3/24 15:26
 */
public class StringReplayDecoder extends ReplayingDecoder<StringReplayDecoder.Status> {

    private int length;

    private byte[] content;

    public StringReplayDecoder() {
        super(Status.PARSE_1);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        switch (state()) {
            case PARSE_1:
                length = byteBuf.readInt();
                System.out.println("接收到的长度: " + length);
                content = new byte[length];
                checkpoint(Status.PARSE_2);
                break;
            case PARSE_2:
                byteBuf.readBytes(content, 0, length);
                list.add(new String(content, StandardCharsets.UTF_8));
                checkpoint(Status.PARSE_1);
                break;
            default:
                break;
        }
    }

    enum Status {
        /**
         * 状态1
         */
        PARSE_1,
        /**
         * 状态2
         */
        PARSE_2
    }
}
