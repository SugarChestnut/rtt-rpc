package cn.rentaotao.netty.protocol.discard;

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
 * @create 2021/3/20 22:48
 */
public class NettyDiscardServer {

    private final int serverPort;

    ServerBootstrap bootstrap = new ServerBootstrap();

    public NettyDiscardServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void runServer() {
        EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerLoopGroup = new NioEventLoopGroup();

        try {
            // 设置反应器线程组
            bootstrap.group(bossLoopGroup, workerLoopGroup);
            // 设置 nio 类型的通道
            bootstrap.channel(NioServerSocketChannel.class);
            // 设置监听端口
            bootstrap.localAddress(serverPort);
            // 设置通道参数
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new NettyDiscardHandler());
                }
            });

            // 开始绑定服务器
            // 通过调用 sync 同步方法阻塞直到绑定成功
            ChannelFuture channelFuture = bootstrap.bind().sync();
            System.out.println("服务器启动成功，监听端口: " + channelFuture.channel().localAddress());
            // 等待通道关闭的异步任务结束
            // 服务监听通道会一直等待通道关闭的异步任务结束
            ChannelFuture closeFuture = channelFuture.channel().closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerLoopGroup.shutdownGracefully();
            bossLoopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8888;
        new NettyDiscardServer(port).runServer();
    }
}
