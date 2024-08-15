package cn.rentaotao.netty.example.decode;

import cn.rentaotao.netty.example.handler.StringDecoder;
import cn.rentaotao.netty.example.handler.StringProcessHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.StandardCharsets;

/**
 * @author rtt
 * @create 2021/3/24 19:44
 */
public class LengthFieldBasedFrameDecoderTest {

    public static final int VERSION = 100;

    static String content = "发哦化工二傻老公danfke各安格斯";

    public static void main(String[] args) throws InterruptedException {
        /*
            maxFrameLength: 发送数据包的最大长度
            lengthFieldOffset: 长度字段偏移量
            lengthFieldLength: 长度字段占用的字节数
            lengthAdjustment: 长度字段的偏移量矫正
            initialBytesToStrip: 丢弃的起始字节数
         */
        LengthFieldBasedFrameDecoder lengthFieldBasedFrameDecoder = new LengthFieldBasedFrameDecoder(
                1024,
                0,
                4,
                4,
                8
        );
        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(lengthFieldBasedFrameDecoder);
                embeddedChannel.pipeline().addLast(new StringDecoder());
                embeddedChannel.pipeline().addLast(new StringProcessHandler());
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        for (int i = 0; i < 10; i++) {
            ByteBuf buffer = Unpooled.buffer();
            String s = i + "次发送 -> " + content;
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            buffer.writeInt(bytes.length);
            buffer.writeInt(10);
            buffer.writeBytes(bytes);
            embeddedChannel.writeInbound(buffer);
        }

        Thread.sleep(1000 * 10);
    }
}
