package cn.rentaotao.netty.chart.server;

import cn.rentaotao.netty.chart.protocol.ImDecoder;
import cn.rentaotao.netty.chart.protocol.ImEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author rtt
 * @date 2023/2/17 23:36
 */
public class ChatServer {

    public void start(int port) {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                  .channel(NioServerSocketChannel.class)
                  .option(ChannelOption.SO_BACKLOG, 1024)
                  .childHandler(new ChannelInitializer<SocketChannel>() {
                      @Override
                      protected void initChannel(SocketChannel ch) throws Exception {
                          System.out.println("Server pipeline init");
                          ChannelPipeline pipeline = ch.pipeline();
                          // 解析自定义协议
                          // Inbound
                          pipeline.addLast(new ImDecoder());
                          // Outbound
                          pipeline.addLast(new ImEncoder());
                          // Inbound
                          pipeline.addLast(new TerminalServerHandler());
                          // 解析 HTTP 请求
                          // Outbound
                          pipeline.addLast(new HttpServerCodec());
                          // Inbound
                          // 将同一个 HTTP 请求或响应的多个消息对象变成一个 fullHttpRequest 完整的消息对象
                          pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                          // Inbound、Outbound
                          // 主要用于处理大数据流，比如 1GB 的文件如果直接传输肯定会占满 JVM 内存，
                          // 加上这个 Handler 就不用考虑这个问题了
                          pipeline.addLast(new ChunkedWriteHandler());
                          // Inbound
                          pipeline.addLast(new HttpServerHandler());

                          // 解析 WebSocket 请求
                          // Inbound
                          pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                          // Inbound
                          pipeline.addLast(new WebSocketServerHandler());
                      }
                  });

            ChannelFuture f = server.bind(port).sync();
            System.out.println("服务器已启动，监听端口：" + port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(8080);
    }
}
