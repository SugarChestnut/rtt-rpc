package cn.rentaotao.jdk.net.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtt
 * @date 2024/8/13 16:02
 */
public class NioClient {

    private SocketChannel client;

    private Selector selector;

    private SelectionKey register;

    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public void connect(String host, int port) throws Exception {
        client = SocketChannel.open();
        selector = Selector.open();
        // 建立连接是否阻塞，直到连接建立成功或者失败
        client.configureBlocking(false);
        register = client.register(selector, SelectionKey.OP_CONNECT);
        client.connect(new InetSocketAddress(host, port));
        /*
            socketChannel.isOpen();                 测试SocketChannel是否为非 close 状态
            socketChannel.isConnected();            测试SocketChannel是否已经连接
            socketChannel.isConnectionPending();    测试SocketChannel是否正在进行连接
            socketChannel.finishConnect();          校验正在进行套接字连接的SocketChannel是否已经完成连接
         */
        while (!client.isConnected()) {}

        while (true) {
            System.out.println("数据处理线程：" + Thread.currentThread());
            // 方法阻塞，直到至少有一个事件发生
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            System.out.println(selectionKeys);
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                if (selectionKey.isReadable()) {
                    try {
                        read(selectionKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (selectionKey.isWritable()) {
                    System.out.println("write: " + selectionKey);
                }
            }
        }
    }

    private void read(SelectionKey key) throws Exception {
        System.out.println("read: " + key);
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int len;
        while ((len = socketChannel.read(buffer)) > 0) {
            System.out.println(new String(buffer.array(), 0, len, StandardCharsets.UTF_8));
            buffer.clear();
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("主线程：" + Thread.currentThread());
        NioClient nioClient = new NioClient();
        nioClient.connect("127.0.0.1", 8587);
        while (true) {
            System.out.println("输入：");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.next();
            System.out.println("发送内容：" + line);
            if (line != null && !line.isEmpty()) {
                try {
                    nioClient.client.write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8)));
                } catch (Exception e) {
                    try {
                        nioClient.client.close();
                    } catch (Exception e1) {
                        // no-op
                    }
                    break;
                }
            }
        }
    }

}
