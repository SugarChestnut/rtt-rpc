package cn.rentaotao.jdk.net.nio;

import cn.rentaotao.common.utils.IOUtils;

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author rtt
 * @date 2024/8/13 16:02
 */
public class NioClient implements Closeable {

    private SocketChannel client;

    private Selector selector;

    private SelectionKey selectionKey;

    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    private volatile boolean running = false;

    public void connect(String host, int port) throws Exception {
        client = SocketChannel.open();
        selector = Selector.open();
        // 建立连接是否阻塞，直到连接建立成功或者失败
        client.configureBlocking(false);
        selectionKey = client.register(selector, SelectionKey.OP_READ);
        boolean connect = client.connect(new InetSocketAddress(host, port));
        if (!connect) {
            // 如果连接失败
            selectionKey.interestOps(SelectionKey.OP_CONNECT);
        }
        /*
            socketChannel.isOpen();                 测试SocketChannel是否为非 close 状态
            socketChannel.isConnected();            测试SocketChannel是否已经连接
            socketChannel.isConnectionPending();    测试SocketChannel是否正在进行连接
            socketChannel.finishConnect();          校验正在进行套接字连接的SocketChannel是否已经完成连接
         */
        while (!client.finishConnect()) {
        }
        running = true;
        while (running) {
            // 方法阻塞，直到至少有一个事件发生
            if (selector.select() == 0) {
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                if (selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    int len;
                    while ((len = socketChannel.read(buffer)) > 0) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, len, StandardCharsets.UTF_8));
                        buffer.clear();
                    }
                }
                iterator.remove();
            }
        }
    }

    @Override
    public void close() {
        IOUtils.closeQuality(selector, client);
        running = false;
    }

    public static void main(String[] args) throws Exception {
        NioClient nioClient = new NioClient();
        new Thread(() -> {
            try {
                nioClient.connect("127.0.0.1", 8587);
            } catch (Exception e) {
                nioClient.close();
            }
        }).start();
        while (true) {
            System.out.println("输入：");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.next();
            System.out.println("发送内容：" + line);
            if (line != null && !line.isEmpty()) {
                try {
                    nioClient.client.write(ByteBuffer.wrap(line.getBytes(StandardCharsets.UTF_8)));
                } catch (Exception e) {
                    nioClient.client.close();
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
