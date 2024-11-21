package cn.rentaotao.netty.coding.decode;

import cn.rentaotao.netty.coding.handler.StringProcessHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @author rtt
 * @create 2021/3/24 15:35
 */
public class StringReplayDecodeTest {

    public static void main(String[] args) {

        String str = "飞好高化工阿红哦豁搞好哥啊和公安韩国IE噢韩国";

        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(new StringReplayDecoder());
                embeddedChannel.pipeline().addLast(new StringProcessHandler());
            }
        };
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        Random random = new Random(3);

        for (int i = 0; i < 10; i++) {
            int i1 = random.nextInt(23);
            String substring = str.substring(0, i1);
            byte[] bytes = substring.getBytes(StandardCharsets.UTF_8);
            ByteBuf buffer = Unpooled.buffer();
            buffer.writeInt(bytes.length);
            buffer.writeBytes(bytes);
            System.out.println("长度: " + bytes.length + ", 内容: " + substring);
            embeddedChannel.writeInbound(buffer);
        }
    }
}
