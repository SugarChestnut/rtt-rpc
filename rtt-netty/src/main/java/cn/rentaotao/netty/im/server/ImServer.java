package cn.rentaotao.netty.im.server;

import cn.rentaotao.netty.im.ProtobufDecode;
import cn.rentaotao.netty.im.ProtobufEncode;
import cn.rentaotao.netty.im.server.handler.HeartBeatServerHandler;
import cn.rentaotao.netty.im.server.handler.LoginRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author rtt
 * @create 2021/3/30 20:21
 */
public class ImServer {

    private final int port = 8888;

    private final EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);

    private final EventLoopGroup workLoopGroup = new NioEventLoopGroup();

    private final ServerBootstrap bootstrap = new ServerBootstrap();

    public void start() {
        try {
            bootstrap.group(bossLoopGroup, workLoopGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast("decoder", new ProtobufDecode())
                                    .addLast("encoder", new ProtobufEncode())
                                    .addLast("heartbeat", new HeartBeatServerHandler())
                                    .addLast("login", new LoginRequestHandler());
                        }
                    });

            ChannelFuture future = bootstrap.bind().sync();

            System.out.println("Im 服务器启动，监听：" + future.channel().localAddress());

            ChannelFuture closeFuture = future.channel().closeFuture();

            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            workLoopGroup.shutdownGracefully();
            bossLoopGroup.shutdownGracefully();
        }
    }

}
