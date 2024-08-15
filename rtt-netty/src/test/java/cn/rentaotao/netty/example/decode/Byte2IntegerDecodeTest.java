package cn.rentaotao.netty.example.decode;

import cn.rentaotao.netty.example.handler.StringProcessHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

/**
 * @author rtt
 * @create 2021/3/24 14:15
 */
public class Byte2IntegerDecodeTest {

    @Test
    public  void test() {
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(new Byte2IntegerReplayDecoder());
                embeddedChannel.pipeline().addLast(new Integer2StringDecoder());
                embeddedChannel.pipeline().addLast(new StringProcessHandler());
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        for (int i = 0; i < 10; i++) {
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeInt(i);
            embeddedChannel.writeInbound(buffer);
        }
    }
}
