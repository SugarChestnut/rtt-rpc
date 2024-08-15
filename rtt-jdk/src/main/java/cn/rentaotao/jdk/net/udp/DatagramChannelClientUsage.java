package cn.rentaotao.jdk.net.udp;

import cn.rentaotao.common.utils.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;

/**
 * UDP 数据传输
 *
 * @author rtt
 * @create 2021/3/14 15:45
 */
public class DatagramChannelClientUsage {

    public static void main(String[] args) throws IOException {

        DatagramChannel datagramChannel = DatagramChannel.open();

        datagramChannel.configureBlocking(false);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        buffer.put("UDP信息".getBytes(StandardCharsets.UTF_8));

        buffer.flip();

        datagramChannel.send(buffer, new InetSocketAddress("127.0.0.1", 8888));

        IOUtils.closeQuality(datagramChannel);
    }
}
