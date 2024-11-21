package cn.rentaotao.netty.coding.decode;

import cn.rentaotao.netty.coding.handler.StringDecoder;
import cn.rentaotao.netty.coding.handler.StringProcessHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author rtt
 * @create 2021/3/24 19:27
 */
public class DelimiterBasedFrameDecoderTest {

    static String splitter = "\t";

    static String content = "广发二个好哦高级还是个会我是狗";

    public static void main(String[] args) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(splitter.getBytes(StandardCharsets.UTF_8));

        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(
                        1024,
                        true,
                        byteBuf
                ));
                embeddedChannel.pipeline().addLast(new StringDecoder());
                embeddedChannel.pipeline().addLast(new StringProcessHandler());
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        Random random = new Random(3);

        for (int i = 0; i < 10; i++) {
            int i1 = random.nextInt(3);
            ByteBuf buffer = Unpooled.buffer();
            for (int k = 0; k < i1; k++) {
                buffer.writeBytes(content.getBytes(StandardCharsets.UTF_8));
            }
            buffer.writeBytes(splitter.getBytes(StandardCharsets.UTF_8));
            embeddedChannel.writeInbound(buffer);
        }

    }
}
