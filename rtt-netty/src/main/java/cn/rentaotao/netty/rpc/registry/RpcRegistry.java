package cn.rentaotao.netty.rpc.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author rtt
 * @date 2023/2/2 10:21
 */
public class RpcRegistry {

    private final int port;

    public RpcRegistry(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        final NioEventLoopGroup workGroup = new NioEventLoopGroup();

        final ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workGroup)
              .channel(NioServerSocketChannel.class)
              .childHandler(new ChannelInitializer<SocketChannel>() {
                  @Override
                  protected void initChannel(SocketChannel socketChannel) throws Exception {
                      final ChannelPipeline pipeline = socketChannel.pipeline();
                      // 帧的最大长度，长度属性的偏移量，长度属性的字节数，要添加到长度属性的补偿值，从解码帧中去除的第一个字节数
                      pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                      pipeline.addLast(new LengthFieldPrepender(4));
                      pipeline.addLast("encoder", new ObjectEncoder());
                      pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                      pipeline.addLast(new RegistryHandler());
                  }
              })
              .option(ChannelOption.SO_BACKLOG, 128)
              .childOption(ChannelOption.SO_KEEPALIVE, true);

        final ChannelFuture f = server.bind(port).sync();
        System.out.println("Registry is started ……");
        f.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        new RpcRegistry(8080).start();
        Class<?>[] classes = {int.class, int.class};
    }
}

