package cn.rentaotao.jdk.net.nio;

import cn.rentaotao.jdk.net.ConnectionHolder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketOption;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtt
 * @date 2024/8/13 16:02
 */
public class NioServer {

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
        register = server.register(selector, SelectionKey.OP_ACCEPT | SelectionKey.OP_READ);
        System.out.println(register);
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
                if (selectionKey.isAcceptable()) {
                    try {
                        accept(selectionKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        iterator.remove();
                        continue;
                    }
                }
                if (selectionKey.isReadable()) {
                    try {
                        read(selectionKey);
                    } catch (Exception e) {
                        e.printStackTrace();
                        iterator.remove();
                        continue;
                    }
                }
                if (selectionKey.isWritable()) {
                    System.out.println("write: " + selectionKey);
                }
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        System.out.println("accept: " + key);
        ConnectionHolder<SocketChannel> holder = new ConnectionHolder<>();
        SocketChannel socketChannel = (SocketChannel) key.channel();
        holder.setConnection(socketChannel);
        holder.setId(UUID.randomUUID().toString());
        long mills = System.currentTimeMillis();
        holder.setConnTime(mills);
        holder.setLastActive(mills);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress)conn.getRemoteAddress();
            String hostAddress = remoteAddress.getAddress().getHostAddress();
            holder.setRemoteAddr(hostAddress);
            cache.put(holder.getId(), holder);
        } finally {
            key.channel().close();
        }
        System.out.println("接受连接：" + holder.getRemoteAddr());
    }

    private void read(SelectionKey key) throws Exception{
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(buffer, socketChannel.shutdownInput());
    }

    public static void main(String[] args) {

    }
}
