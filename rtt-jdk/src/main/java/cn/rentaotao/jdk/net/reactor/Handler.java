package cn.rentaotao.jdk.net.reactor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author rtt
 * @create 2021/3/19 15:07
 */
public final class Handler implements Runnable{

    final SocketChannel socketChannel;

    final SelectionKey key;

    ByteBuffer input = ByteBuffer.allocate(1024);

    ByteBuffer output = ByteBuffer.allocate(1024);

    static final int READING = 0, SENDING = 1;

    volatile int state = READING;

    public Handler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        // 稍后设置感兴趣的IO事件
        key = socketChannel.register(selector, 0);
        key.attach(this);
        // 注册事件
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
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
        while ((len = socketChannel.read(input)) > 0) {
            System.out.println(new String(input.array(), 0, len));
            input.clear();
        }
        key.interestOps(SelectionKey.OP_WRITE);
        state = SENDING;
    }

    void send() throws IOException {
        output.put("服务应答".getBytes());
        output.flip();
        socketChannel.write(output);
        output.clear();
        key.interestOps(SelectionKey.OP_READ);
        state = READING;
    }

//    boolean inputIsComplete() {
//        return this.state == SENDING;
//    }
//
//    boolean outputIsComplete() {
//        return this.state == READEING;
//    }

    void process() {}
}
