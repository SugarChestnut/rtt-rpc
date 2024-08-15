package cn.rentaotao.jdk.net.nio;

import cn.rentaotao.common.utils.IOUtils;
import cn.rentaotao.jdk.net.ConnectionHolder;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtt
 * @date 2024/8/13 16:02
 */
public class NioServer implements Closeable {
    /**
     * ServerSocketChannel 不具备读写数据的能力，和客户端交互是通过 SocketChannel 实现的.
     */
    private ServerSocketChannel server;

    private final ConcurrentHashMap<String, ConnectionHolder<SocketChannel>> cache = new ConcurrentHashMap<>();

    private Selector selector;

    private final int port;

    private SelectionKey register;

    private final ByteBuffer buffer = ByteBuffer.allocate(1024);

    private volatile boolean running = false;

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
        running = true;
        System.out.println("启动服务端，端口号：" + port);
        while (running) {
            // 方法阻塞，直到至少有一个事件发生
            if (selector.select() == 0) {
                System.out.println(0);
                continue;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                try {
                    if (selectionKey.isAcceptable()) {
                        System.out.println("accept: " + selectionKey);
                        accept(selectionKey);
                    }
                    if (selectionKey.isReadable()) {
                        System.out.println("read: " + selectionKey);
                        read(selectionKey);
                    }
                    if (selectionKey.isWritable()) {
                        System.out.println("write: " + selectionKey);
                        write(selectionKey);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iterator.remove();
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ConnectionHolder<SocketChannel> holder = new ConnectionHolder<>();
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        holder.setConnection(socketChannel);
        holder.setId(UUID.randomUUID().toString());
        long mills = System.currentTimeMillis();
        holder.setConnTime(mills);
        holder.setLastActive(mills);
        InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
        String hostAddress = remoteAddress.getAddress().getHostAddress();
        holder.setRemoteAddr(hostAddress);
        holder.setPort(remoteAddress.getPort());
        cache.put(holder.getId(), holder);
        // 将客户端连接注册到 selector
        socketChannel.register(selector, SelectionKey.OP_READ, holder);
        System.out.println("接受连接：" + holder.getRemoteAddr() + ":" + holder.getPort());
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
        socketChannel.write(ByteBuffer.wrap("收到".getBytes(StandardCharsets.UTF_8)));
//        key.interestOpsOr(SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key) {
        // 写事件是一直能触发的，
        @SuppressWarnings("unchecked")
        ConnectionHolder<SocketChannel> attachment = (ConnectionHolder<SocketChannel>) key.attachment();
        attachment.setLastActive(System.currentTimeMillis());
        System.out.println(attachment.getContent());
        key.interestOpsAnd(~SelectionKey.OP_WRITE);
    }

    @Override
    public void close() {
        IOUtils.closeQuality(selector, server);
    }

    public static void main(String[] args) throws Exception {
        try(NioServer nioServer = new NioServer(8587)) {
            nioServer.start();
        }
    }
}
