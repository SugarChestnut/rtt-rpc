package cn.rentaotao.netty.coding.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * @author rtt
 * @create 2021/3/23 10:12
 */
public class InHandlerTest {

    public static void main(String[] args) throws InterruptedException {
        InHandlerOne inHandlerOne = new InHandlerOne();
        InHandlerTwo inHandlerTwo = new InHandlerTwo();

        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {

            @Override
            protected void initChannel(EmbeddedChannel embeddedChannel) throws Exception {
                embeddedChannel.pipeline().addLast(inHandlerOne, inHandlerTwo);
            }
        };

        EmbeddedChannel embeddedChannel = new EmbeddedChannel(channelInitializer);

        ByteBuf buffer = Unpooled.buffer();

        buffer.writeInt(1);

        embeddedChannel.writeInbound(buffer);
//        embeddedChannel.finish();

        embeddedChannel.writeInbound(buffer);
        embeddedChannel.finish();


        Thread.sleep(1000 * 100);
        embeddedChannel.close();
    }
}
