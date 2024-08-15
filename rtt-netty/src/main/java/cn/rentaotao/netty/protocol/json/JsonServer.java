package cn.rentaotao.netty.protocol.json;

import cn.rentaotao.common.utils.JsonUtils;
import cn.rentaotao.netty.Message;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author rtt
 * @create 2021/3/25 10:22
 */
public class JsonServer {

    public void runServer() {
        NioEventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workLoopGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 绑定反应器
            serverBootstrap.group(bossLoopGroup, workLoopGroup);
            // 设置通道类型
            serverBootstrap.channel(NioServerSocketChannel.class);
            // 设置监听端口
            serverBootstrap.localAddress(8888);
            // 设置通道参数
            serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(
                            1024,
                            0,
                            4,
                            0,
                            4
                    ));
                    socketChannel.pipeline().addLast(new StringDecoder());
                    socketChannel.pipeline().addLast(new JsonMsgDecoder());
                }
            });

            ChannelFuture future = serverBootstrap.bind().sync();
            System.out.println("服务器启动成功，监听端口: " + future.channel().localAddress());
            future.channel().closeFuture();
            future.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossLoopGroup.shutdownGracefully();
            workLoopGroup.shutdownGracefully();
        }
    }

    static class JsonMsgDecoder extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            String json = (String) msg;
            Message message = JsonUtils.string2Object(json, Message.class);
            System.out.println(message.toString());
        }
    }

    public static void main(String[] args) {
        new JsonServer().runServer();
    }
}
