package cn.rentaotao.jdk.net.nio;

import cn.rentaotao.jdk.net.ConnectionHolder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * https://blog.csdn.net/anlian523/article/details/105020210
 *
 * @author rtt
 * @date 2024/8/13 16:02
 */
public class NioServer {
    /**
     * ServerSocketChannel 不具备读写数据的能力，和客户端交互是通过 SocketChannel 实现的.
     */
    private ServerSocketChannel server;

    private final ConcurrentHashMap<String, ConnectionHolder<SocketChannel>> cache = new ConcurrentHashMap<>();

    private Selector selector;

    private final int port;

    private SelectionKey register;

    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    public NioServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        server = ServerSocketChannel.open();
        // 注册选择器的时候必须设置为非阻塞模式
        server.configureBlocking(false);
        server.bind(new InetSocketAddress(port));
        selector = Selector.open();
        register = server.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println(register);
        while (true) {
            System.out.println("数据处理线程：" + Thread.currentThread());
            // 方法阻塞，直到至少有一个事件发生
            if (selector.select() == 0) {
                System.out.println(0);
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                System.out.println(selectionKey);
                iterator.remove();
                if (selectionKey.isAcceptable()) {
                    System.out.println("accept: " + selectionKey);
                    try {
                        accept(selectionKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (selectionKey.isReadable()) {
                    System.out.println("read: " + selectionKey);
                    try {
                        read(selectionKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (selectionKey.isWritable()) {
                    System.out.println("write: " + selectionKey);
                }
                if (selectionKey.isConnectable()) {
                    System.out.println("connect: " + selectionKey);
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ConnectionHolder<SocketChannel> holder = new ConnectionHolder<>();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.configureBlocking(false);
        holder.setConnection(socketChannel);
        holder.setId(UUID.randomUUID().toString());
        long mills = System.currentTimeMillis();
        holder.setConnTime(mills);
        holder.setLastActive(mills);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            String hostAddress = remoteAddress.getAddress().getHostAddress();
            holder.setRemoteAddr(hostAddress);
            cache.put(holder.getId(), holder);
        } finally {
            key.channel().close();
        }
        // 将客户端连接注册到 selector
        socketChannel.register(selector, SelectionKey.OP_READ, holder);
        System.out.println("接受连接：" + holder.getRemoteAddr());
    }

    private void read(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        int len;
        while ((len = socketChannel.read(buffer)) > 0) {
            System.out.println(new String(buffer.array(), 0, len, StandardCharsets.UTF_8));
            buffer.clear();
        }
        @SuppressWarnings("unchecked")
        ConnectionHolder<SocketChannel> attachment = (ConnectionHolder<SocketChannel>) key.attachment();
        attachment.setLastActive(System.currentTimeMillis());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("主线程：" + Thread.currentThread());
        NioServer nioServer = new NioServer(8587);
        nioServer.start();
        System.out.println(1);
    }
}
