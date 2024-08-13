package cn.rentaotao.jdk.net.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * @author rtt
 * @date 2024/8/13 09:45
 */
public class AioClient {

    private final AsynchronousSocketChannel client;

    public AioClient() throws IOException {
        this.client = AsynchronousSocketChannel.open();
    }

    public void Connect(String host, int port) throws Exception {
        client.connect(new InetSocketAddress(host, port)).get();

        final ByteBuffer buf = ByteBuffer.allocate(1024);

        client.read(buf, null, new CompletionHandler<>() {

            @Override
            public void completed(Integer result, Object attachment) {
                System.out.println("执行线程：" + Thread.currentThread());
                // 小于0表示没有可用数据
                if (result > 0) {
                    System.out.println("接受内容：" + new String(buf.array(), 0, result, StandardCharsets.UTF_8));
                    buf.clear();
                }
                // 再次获取内容
                client.read(buf, null, this);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                try {
                    client.close();
                } catch (Exception e1) {
                    // no-op
                }
            }
        });
    }

    public static void main(String[] args) throws Exception {
        System.out.println("主线程：" + Thread.currentThread());
        AioClient aioClient = new AioClient();
        aioClient.Connect("127.0.0.1", 8587);

        while (true) {
            System.out.println("输入：");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.next();
            System.out.println("发送内容：" + line);
            if (line != null && !line.isEmpty()) {
                try {
                    aioClient.client.write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8))).get();
                } catch (Exception e) {
                    try {
                        aioClient.client.close();
                    } catch (Exception e1) {
                        // no-op
                    }
                    break;
                }
            }
        }
    }
}
