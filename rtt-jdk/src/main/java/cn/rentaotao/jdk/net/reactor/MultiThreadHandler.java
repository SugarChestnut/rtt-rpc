package cn.rentaotao.jdk.net.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @create 2021/3/19 23:18
 */
public class MultiThreadHandler implements Runnable {

    final SocketChannel socketChannel;

    final SelectionKey key;

    final ByteBuffer buffer = ByteBuffer.allocate(1024);

    static final int READING = 0;

    static final int SENDING = 1;

    int state = READING;

    static ExecutorService pool = new ThreadPoolExecutor(
            4,
            4,
            0,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1000),
            r -> {
                Thread thread = new Thread(r);
                thread.setName("HandlerThread");
                return thread;
            });

    public MultiThreadHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        key = this.socketChannel.register(selector, 0);
        key.attach(this);
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        pool.execute(new AsyncTask());
    }

    public synchronized void asyncRun() {
        try {
            if (state == READING) {
                read();
            } else if (state == SENDING) {
                send();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void read() throws IOException {
        int len;
        buffer.clear();
        while ((len = socketChannel.read(buffer)) > 0) {
            System.out.println(new String(buffer.array(), 0, len));
            buffer.clear();
        }
        key.interestOps(SelectionKey.OP_WRITE);
        state = SENDING;
    }

    void send() throws IOException {
        buffer.clear();
        buffer.put("服务应答".getBytes());
        buffer.flip();
        socketChannel.write(buffer);
        buffer.clear();
        key.interestOps(SelectionKey.OP_READ);
        state = READING;
    }

    class AsyncTask implements Runnable {

        @Override
        public void run() {
            MultiThreadHandler.this.asyncRun();
        }
    }
}
