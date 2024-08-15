package cn.rentaotao.netty.protocol.discard;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author rtt
 * @create 2021/3/24 10:33
 */
public class NettyClient {

    private final int serverPort;

    private final String serverIp;

    private final Bootstrap bootstrap = new Bootstrap();

    public NettyClient(String ip, int port) {
        this.serverPort = port;
        this.serverIp = ip;
    }

    public void runClient() {
        // 创建反应器线程组
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            // 设置反应器线程组
            bootstrap.group(eventLoopGroup);
            // 设置通道类型
            bootstrap.channel(NioSocketChannel.class);
            // 设置服务器地址
            bootstrap.remoteAddress(serverIp, serverPort);
            // 设置通道参数
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            // 流水线设置
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new NettyClientHandler());
                }
            });
            // 连接
            ChannelFuture future = bootstrap.connect();
            // 添加监听器
            future.addListener(f -> {
                if (f.isSuccess()) {
                    System.out.println("服务器连接成功");
                } else {
                    System.out.println("服务器连接失败");
                }
            });
            // 阻塞
            future.sync();
            // 获取通道
            Channel channel = future.channel();
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("请输入发送内容: ");
//            while (scanner.hasNext()) {
//                String str = scanner.next();
//                byte[] bytes = (LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " >> " + str).getBytes(StandardCharsets.UTF_8);
//                ByteBuf buffer = channel.alloc().buffer();
//                buffer.writeBytes(bytes);
//                // 写入数据
//                channel.writeAndFlush(buffer);
//                System.out.println("请输入发送内容: ");
//            }

            for (int i = 1; i < 100; i++) {
                ByteBuf buffer = channel.alloc().buffer();
                buffer.writeBytes("这是一段测试文本".getBytes());
                channel.writeAndFlush(buffer);
            }

            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            eventLoopGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyClient("127.0.0.1", 8888).runClient();
    }
}
