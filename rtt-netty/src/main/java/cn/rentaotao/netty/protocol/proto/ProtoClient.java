package cn.rentaotao.netty.protocol.proto;

import cn.rentaotao.common.proto.Msg;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * @author rtt
 * @create 2021/3/25 20:23
 */
public class ProtoClient {

    public void runClient() {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(loopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.remoteAddress("127.0.0.1", 8888);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                    ch.pipeline().addLast(new ProtobufEncoder());
                }
            });

            ChannelFuture future = bootstrap.connect();
            future.addListener(f -> {
                if (f.isSuccess()) {
                    System.out.println("服务器连接成功");
                } else {
                    System.out.println("服务器连接失败");
                }
            });
            future.sync();

            Channel channel = future.channel();
            for (int i = 0; i < 10; i++) {
                Msg.Builder builder = Msg.newBuilder();
                builder.setId(i);
                builder.setContent("消息" + i);
                Msg msg = builder.build();
                channel.writeAndFlush(msg);
            }

            channel.flush();

            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            loopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ProtoClient().runClient();
    }
}
