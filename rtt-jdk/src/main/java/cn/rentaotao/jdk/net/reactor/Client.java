package cn.rentaotao.jdk.net.reactor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

/**
 * @author rtt
 * @create 2021/3/19 15:53
 */
public class Client {

    public void start() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        System.out.println("客户端启动成功");
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8888));
        while (!socketChannel.finishConnect()) {}
        System.out.println("客户端连接成功");

        Processor processor = new Processor(socketChannel);
        new Thread(processor).start();
    }

    static class Processor implements Runnable {

        final Selector selector;

        final SocketChannel socketChannel;

        public Processor(SocketChannel socketChannel) throws IOException {
            selector = Selector.open();
            this.socketChannel = socketChannel;
            this.socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    selector.select();
                    Set<SelectionKey> keySet = selector.selectedKeys();
                    for (SelectionKey key : keySet) {
                        /*
                            还没有实现接收数据
                         */
                        if (key.isReadable()) {
                            int len;
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            SocketChannel channel = (SocketChannel) key.channel();
                            while ((len = channel.read(buffer)) > 0) {
                                buffer.flip();
                                System.out.println(new String(buffer.array(), 0, len));
                                buffer.clear();
                            }
                        }

                        if (key.isWritable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            Scanner scanner = new Scanner(System.in);
                            System.out.println("请输入内容：");
                            if (scanner.hasNext()) {
                                SocketChannel channel = (SocketChannel) key.channel();
                                String str = scanner.next();
                                buffer.put(str.getBytes());
                                buffer.flip();
                                channel.write(buffer);
                                buffer.clear();
                            }
                        }


                    }

                    keySet.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Client().start();
    }
}
