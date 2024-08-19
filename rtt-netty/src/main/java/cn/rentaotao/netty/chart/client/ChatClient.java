package cn.rentaotao.netty.chart.client;

import cn.rentaotao.netty.chart.ImDecoder;
import cn.rentaotao.netty.chart.ImEncoder;
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

    private final String nickName;

    private String host;

    private int port;

    public ChatClient(String nickName) {
        this.nickName = nickName;
    }

    public void connect(String host, int port) throws Exception{
        this.host = host;
        this.port = port;
        connect0();
    }

    private void connect0() throws Exception{
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
                            .addLast(new ChatClientHandler(nickName));
                }
            });
            ChannelFuture f = b.connect(host, port);
            System.out.println("等待连接……");
            f.sync();
            System.out.println("等待关闭");
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new ChatClient("rtt").connect("127.0.0.1", 8080);
    }
}
