package cn.rentaotao.netty.protocol.proto;

import cn.rentaotao.common.proto.Msg;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;

/**
 * @author rtt
 * @create 2021/3/25 20:06
 */
public class ProtoServer {

    public void startServer() {
        NioEventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossLoopGroup, workLoopGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.localAddress(8888);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast(new ProtobufDecoder(Msg.getDefaultInstance()));
                    ch.pipeline().addLast(new ProtobufBusinessDecode());
                }
            });

            ChannelFuture channelFuture = bootstrap.bind().sync();
            System.out.println("服务器启动成功，监听端口: " + channelFuture.channel().localAddress());

            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossLoopGroup.shutdownGracefully();
            workLoopGroup.shutdownGracefully();
        }
    }

    static class ProtobufBusinessDecode extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Msg m = (Msg) msg;
            System.out.println(m.getContent());
            super.channelRead(ctx, msg);
        }
    }

    public static void main(String[] args) {
        new ProtoServer().startServer();
    }
}
