package cn.rentaotao.netty.example.encode;

import cn.rentaotao.netty.example.encode.Integer2ByteEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

/**
 * @author rtt
 * @create 2021/3/25 09:03
 */
public class Integer2ByteEncoderTest {

    @Test
    public void testEncode() throws Exception {
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(new Integer2ByteEncoder());
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        for (int i = 0; i < 10; i++) {
            embeddedChannel.write(i);
        }

        embeddedChannel.flush();

        ByteBuf byteBuf = embeddedChannel.readOutbound();

        while (null != byteBuf) {
            System.out.println(byteBuf.readInt());
            byteBuf = embeddedChannel.readOutbound();
        }

        Thread.sleep(1000 * 10);
    }
}
