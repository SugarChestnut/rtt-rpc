package cn.rentaotao.netty.example.decode;

import cn.rentaotao.netty.example.handler.StringDecoder;
import cn.rentaotao.netty.example.handler.StringProcessHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author rtt
 * @create 2021/3/24 16:30
 */
public class LineBasedFrameDecoderTest {

    public static void main(String[] args) {
        String str = "飞好高化工阿红哦豁搞好哥啊和公安韩国IE噢韩国";

        String seporate = "\r\n";

        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
                embeddedChannel.pipeline().addLast(new StringDecoder());
                embeddedChannel.pipeline().addLast(new StringProcessHandler());
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        Random random = new Random(3);

        for (int i = 0; i < 10; i++) {
            int i1 = random.nextInt(23);
            String substring = str.substring(0, i1);
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeBytes(substring.getBytes(StandardCharsets.UTF_8));
            buffer.writeBytes(seporate.getBytes(StandardCharsets.UTF_8));
            embeddedChannel.writeInbound(buffer);
        }
    }
}
