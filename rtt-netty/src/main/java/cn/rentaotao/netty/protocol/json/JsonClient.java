package cn.rentaotao.netty.protocol.json;

import cn.rentaotao.common.utils.JsonUtils;
import cn.rentaotao.netty.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @author rtt
 * @create 2021/3/25 10:58
 */
public class JsonClient {

    public void runClient() {
        NioEventLoopGroup loopGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            // 设置反应器线程组
            bootstrap.group(loopGroup);
            // 设置通道类型
            bootstrap.channel(NioSocketChannel.class);
            // 设置服务器地址
            bootstrap.remoteAddress("127.0.0.1", 8888);
            // 设置通道参数
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            // 装配流水线通道
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    // 在 hard-content 协议开头添加内容长度
                    socketChannel.pipeline().addLast(new LengthFieldPrepender(4));
                    socketChannel.pipeline().addLast(new StringEncoder());
                }
            });
            // 连接服务器
            ChannelFuture future = bootstrap.connect();

            future.addListener(futureListener -> {
                if (futureListener.isSuccess()) {
                    System.out.println("客户端连接成功");
                } else {
                    System.out.println("客户端连接失败");
                }
            });
            // 阻塞直到连接完成
            future.sync();

            Channel channel = future.channel();
            for (int i = 0; i < 10; i++) {
                Message message = new Message();
                message.setId(i);
                message.setContent("消息" + i);
                channel.writeAndFlush(JsonUtils.object2String(message));
            }

            channel.flush();
            /*
                等待通道关闭的异步任务结束
                服务监听通道会一直等待通道关闭的异步任务结束
             */
            ChannelFuture closeFuture = channel.closeFuture();
            closeFuture.sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new JsonClient().runClient();
    }
}
