package cn.rentaotao.jdk.net.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rtt
 * @create 2021/3/19 16:51
 */
public class MultiThreadReactor {

    ServerSocketChannel serverSocketChannel;

    AtomicInteger next = new AtomicInteger(0);

    Selector[] selectors = new Selector[2];

    SubReactor[] subReactors;

    public MultiThreadReactor() throws IOException {
        selectors[0] = Selector.open();
        selectors[1] = Selector.open();

        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8888));
        SelectionKey selectionKey = serverSocketChannel.register(selectors[0], SelectionKey.OP_ACCEPT);
        selectionKey.attach(new Acceptor());

        SubReactor subReactor0 = new SubReactor(selectors[0]);
        SubReactor subReactor1 = new SubReactor(selectors[1]);
        subReactors = new SubReactor[] {subReactor0, subReactor1};
    }

    public void startService() {
        /*
            当多个线程同时使用一个 selector 的时候，无法接收到数据，会卡死？？
         */
        Thread thread1 = new Thread(subReactors[0]);
        thread1.setName("线程1");
        thread1.start();
        Thread thread2 = new Thread(subReactors[1]);
        thread2.setName("线程2");
        thread2.start();


    }

    class SubReactor implements Runnable {

        final Selector selector;

        public SubReactor(Selector selector) {
            this.selector = selector;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    System.out.println("-" + Thread.currentThread().getName());
                    selector.select();
                    System.out.println("--" + Thread.currentThread().getName());
                    Set<SelectionKey> keySet = selector.selectedKeys();
                    for (SelectionKey key : keySet) {
                        dispatch(key);
                    }
                    keySet.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void dispatch(SelectionKey key) {
            Runnable r = (Runnable) key.attachment();
            if (r != null) {
                r.run();
            }
        }
    }

    class Acceptor implements Runnable {

        @Override
        public void run() {
            try {
                SocketChannel socketChannel = serverSocketChannel.accept();
                if (socketChannel != null) {
                    new MultiThreadHandler(selectors[next.get()], socketChannel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (next.incrementAndGet() == selectors.length) {
                next.set(0);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new MultiThreadReactor().startService();
    }
}
