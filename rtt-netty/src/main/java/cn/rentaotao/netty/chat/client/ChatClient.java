package cn.rentaotao.netty.chat.client;

import cn.rentaotao.netty.chat.protocol.ImDecoder;
import cn.rentaotao.netty.chat.protocol.ImEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author rtt
 * @date 2023/2/20 16:50
 */
public class ChatClient {

    private final ChatClientHandler clientHandler;

    private String host;

    private int port;

    public ChatClient(String nickName) {
        this.clientHandler = new ChatClientHandler(nickName);
    }

    public void connect(String host, int port) {
        this.host = host;
        this.port = port;
        connect0();
    }

    private void connect0() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline()
                      .addLast(new ImDecoder())
                      .addLast(new ImEncoder())
                      .addLast(clientHandler);
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
            ChannelFuture channelFuture = f.channel().closeFuture();
            channelFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        // new ChatClient("rtt").connect("127.0.0.1", 8080);
        new ChatClient("yyl").connect("127.0.0.1", 8080);
    }
}
