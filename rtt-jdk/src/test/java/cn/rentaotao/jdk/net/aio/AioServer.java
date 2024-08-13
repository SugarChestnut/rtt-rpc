package cn.rentaotao.jdk.net.aio;

import lombok.Data;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rtt
 * @date 2024/8/13 09:45
 */
public class AioServer {

    private AsynchronousServerSocketChannel server;

    private final ConcurrentHashMap<String, ConnectionHolder<AsynchronousSocketChannel>> cache = new ConcurrentHashMap<>();

    private final int port;

    public AioServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        // 当接收客户端的数据的时候，使用线程池执行回调函数
        ExecutorService executor = Executors.newCachedThreadPool(new ThreadFactory() {
            final AtomicInteger id = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                int v = this.id.getAndIncrement();
                if (v > 1 << 8) {
                    v  = 1;
                    id.set(v + 1);
                }
                thread.setName("pool-thread-socket-" + v);
                return thread;
            }
        });
        AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withCachedThreadPool(executor, 1);
        // 创建异步socket
        server = AsynchronousServerSocketChannel.open(channelGroup);
        // 绑定到本地端口
        server.bind(new InetSocketAddress(port));
        long start = System.currentTimeMillis();
        // 等待绑定成功
        while (!server.isOpen()) if (start + 100 > System.currentTimeMillis()) break;
        System.out.println("启动成功，监听端口: " + port);
        /*
            异步操作，获取一个连接，不会阻塞
            多线程执行的时候，只有一个 accept 会被执行
         */
        server.accept(UUID.randomUUID().toString(), new CompletionHandler<>() {

            @Override
            public void completed(AsynchronousSocketChannel conn, String attachment) {
                System.out.println("获取连接线程：" + Thread.currentThread());
                final CompletionHandler<AsynchronousSocketChannel, String> outer = this;
                ConnectionHolder<AsynchronousSocketChannel> holder = new ConnectionHolder<>();
                long millis = System.currentTimeMillis();
                holder.setConnTime(millis);
                holder.setLastActive(millis);
                holder.setId(attachment);
                holder.setConnection(conn);
                try {
                    InetSocketAddress remoteAddress = (InetSocketAddress)conn.getRemoteAddress();
                    String hostAddress = remoteAddress.getAddress().getHostAddress();
                    holder.setRemoteAddr(hostAddress);
                    cache.put(attachment, holder);
                } catch (Exception e) {
                    this.failed(e, attachment);
                    return;
                }

                System.out.println("接受连接：" + holder.getRemoteAddr());

                // 获取下一个连接
                server.accept(UUID.randomUUID().toString(), this);

                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                // TODO 如果缓存不够怎么办
                conn.read(buffer, attachment, new CompletionHandler<>() {

                    @Override
                    public void completed(Integer len, String attachment) {
                        System.out.println("数据处理线程：" + Thread.currentThread());
                        if (len > 0) {
                            System.out.println(attachment + ":" + new String(buffer.array(), 0, len));
                        }
                        buffer.clear();
                        try {
                            conn.write(ByteBuffer.wrap(LocalDateTime.now().toString().getBytes(StandardCharsets.UTF_8))).get();
                        } catch (Exception e) {
                            failed(e, attachment);
                        }
                        if (conn.isOpen()) {
                            conn.read(buffer, attachment, this);
                        }
                    }

                    @Override
                    public void failed(Throwable exc, String attachment) {
                        if (conn.isOpen()) return;
                        outer.failed(exc, attachment);
                    }
                });
            }

            @Override
            public void failed(Throwable exc, String attachment) {
                System.out.println("处理连接失败");
                ConnectionHolder<AsynchronousSocketChannel> holder = cache.get(attachment);
                if (holder != null) {
                    try {
                        holder = cache.remove(attachment);
                        holder.getConnection().close();
                    } catch (Exception e) {
                        // no-op
                    }
                }
            }
        });
    }

    @Data
    static class ConnectionHolder<T> {
        T connection;
        long connTime;
        long lastActive;
        String remoteAddr;
        String id;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("主线程：" + Thread.currentThread());
        AioServer aioServer = new AioServer(8587);
        aioServer.start();
        System.out.println(1);
        if (aioServer.server.isOpen()) {
            new CountDownLatch(1).await();
        } else {
            for (Map.Entry<String, ConnectionHolder<AsynchronousSocketChannel>> entry : aioServer.cache.entrySet()) {
                ConnectionHolder<AsynchronousSocketChannel> holder = entry.getValue();
                AsynchronousSocketChannel connection = holder.getConnection();
                connection.close();
            }
            aioServer.server.close();
        }
    }
}
