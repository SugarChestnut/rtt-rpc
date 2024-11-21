package cn.rentaotao.netty.coding.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @author rtt
 * @create 2021/3/24 14:52
 */
public class Byte2IntegerReplayDecoder extends ReplayingDecoder<Byte2IntegerReplayDecoder.Status> {

    /**
     * 第一次读取结果
     */
    private int first;

    /**
     * 第二次读取结果
     */
    private int second;

    public Byte2IntegerReplayDecoder() {
        // 设置初始状态
        super(Status.PARSE_1);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        switch (state()) {
            case PARSE_1:
                first = byteBuf.readInt();
                // 转换状态
                checkpoint(Status.PARSE_2);
                break;
            case PARSE_2:
                second = byteBuf.readInt();
                Integer sum = first + second;
                list.add(sum);
                // 转换状态
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
