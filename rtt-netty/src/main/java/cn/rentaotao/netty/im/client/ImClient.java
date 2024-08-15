package cn.rentaotao.netty.im.client;

import cn.rentaotao.netty.im.ProtobufDecode;
import cn.rentaotao.netty.im.ProtobufEncode;
import cn.rentaotao.netty.im.client.handler.ChatMsgHandler;
import cn.rentaotao.netty.im.client.handler.LoginResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * @author rtt
 * @create 2021/3/30 14:52
 */
public class ImClient {

    private final String ip = "127.0.0.1";

    private final int port = 8888;

    private final Bootstrap bootstrap = new Bootstrap();

    private EventLoopGroup loopGroup = new NioEventLoopGroup();

    private GenericFutureListener<ChannelFuture> listener;

    public ImClient(GenericFutureListener<ChannelFuture> listener) {
        this.listener = listener;
    }

    public void connect() {

        bootstrap.group(loopGroup);
        bootstrap.remoteAddress(ip, port);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        // 一定要注意出入站的顺序问题
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast("encoder", new ProtobufEncode());
                ch.pipeline().addLast("decoder", new ProtobufDecode());
                ch.pipeline().addLast("chat", new ChatMsgHandler());
                ch.pipeline().addLast("login", new LoginResponseHandler());
            }
        });

        System.out.println("客户端开始连接……");

        ChannelFuture future = bootstrap.connect();

        future.addListener(listener);
    }

    public void close() {
        loopGroup.shutdownGracefully();
    }
}
