package cn.rentaotao.jdk.net.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * @author rtt
 * @create 2021/3/19 14:22
 */
public class Reactor implements Runnable {

    final Selector selector;

    final ServerSocketChannel serverSocketChannel;

    public Reactor(int port) throws IOException {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // IO
                selector.select();
                Set<SelectionKey> keySet = selector.selectedKeys();
                for (SelectionKey key : keySet) {
                    // if (key.isAcceptable())
                    dispatch(key);
                }
                keySet.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void dispatch(SelectionKey k) {
        // acceptor、handler
        Runnable r = (Runnable) k.attachment();
        if (r != null) {
            r.run();
        }
    }

    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new Handler(selector, socketChannel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) throws IOException {
        new Reactor(8888).run();
    }
}
