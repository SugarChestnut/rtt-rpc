package cn.rentaotao.jdk.net.udp;

import cn.rentaotao.common.utils.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

/**
 * @author rtt
 * @create 2021/3/14 16:55
 */
public class DatagramChannelServerUsage {

    public static void main(String[] args) throws IOException, InterruptedException {

        DatagramChannel channel = DatagramChannel.open();

        channel.configureBlocking(false);

        channel.bind(new InetSocketAddress("127.0.0.1", 8888));

        // 调用静态工厂方法来获取 Selector 实例
        Selector selector = Selector.open();

        channel.register(selector, SelectionKey.OP_READ);

        while (selector.select() > 0) {
            System.out.println(selector.selectedKeys().toString());
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

            ByteBuffer buffer = ByteBuffer.allocate(1024);

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                /*
                    有问题，该事件不一定在 channel 通道中发生
                 */
                if (key.isReadable()) {
                    channel.receive(buffer);
                    buffer.flip();
                    System.out.println(new String(buffer.array(), 0, buffer.limit()));
                    buffer.clear();
                }
            }

            iterator.remove();
        }
        IOUtils.closeQuality(selector, channel);
    }
}
