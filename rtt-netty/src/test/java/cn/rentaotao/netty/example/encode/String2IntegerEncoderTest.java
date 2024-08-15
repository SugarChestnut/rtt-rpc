package cn.rentaotao.netty.example.encode;

import cn.rentaotao.netty.example.encode.Integer2ByteEncoder;
import cn.rentaotao.netty.example.encode.String2IntegerEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

/**
 * @author rtt
 * @create 2021/3/25 09:20
 */
public class String2IntegerEncoderTest {

    @Test
    public  void testEncode() throws Exception {
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(new Integer2ByteEncoder());
                embeddedChannel.pipeline().addLast(new String2IntegerEncoder());
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        embeddedChannel.write("shrhsfs3252gdh4hberh");

        embeddedChannel.flush();

        ByteBuf byteBuf = embeddedChannel.readOutbound();

        while (null != byteBuf) {
            System.out.println(byteBuf.readInt());
            byteBuf = embeddedChannel.readOutbound();
        }

        Thread.sleep(5000);
    }
}
