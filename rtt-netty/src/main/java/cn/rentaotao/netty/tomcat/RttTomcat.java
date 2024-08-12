package cn.rentaotao.netty.tomcat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author rtt
 * @date 2023/2/1 14:21
 */
public class RttTomcat {

    private int port;

    private final Map<String, Servlet> servletMapping = new HashMap<>();

    private void init() throws Exception {
        final String path = Objects.requireNonNull(this.getClass().getResource("")).getPath();
        String filePath = path + "web.properties";
        final Properties properties = new Properties();
        properties.load(new FileInputStream(filePath));
        for (Object k : properties.keySet()) {
            String key = k.toString();
            if (key.endsWith("port")) {
                port = Integer.parseInt(properties.getProperty(key));
            }
            if (key.endsWith("url")) {
                String servletName = key.replaceAll("\\.url$", "");
                String url = properties.getProperty(key);
                String className = properties.getProperty(servletName + ".class");
                Servlet servlet = (Servlet) Class.forName(className).getDeclaredConstructor().newInstance();
                servletMapping.put(url, servlet);
            }
        }
    }

    public void start() throws Exception {
        init();

        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        final ServerBootstrap server = new ServerBootstrap();

        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new HttpResponseEncoder());
                        socketChannel.pipeline().addLast(new HttpRequestDecoder());
                        socketChannel.pipeline().addLast(new RttTomcatHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        final ChannelFuture f = server.bind(port).sync();
        System.out.println("服务器启动成功……");
        f.channel().closeFuture().sync();
    }

    class RttTomcatHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest httpRequest = (HttpRequest) msg;
                final RttRequest rttRequest = new RttRequest(httpRequest);
                final RttResponse rttResponse = new RttResponse(ctx);

                final String url = rttRequest.getUrl();
                System.out.println("url: " + url);

                if (servletMapping.containsKey(url)) {
                    servletMapping.get(url).service(rttRequest, rttResponse);
                } else {
                    rttResponse.write("404 - Not Found");
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        new RttTomcat().start();
    }

}
