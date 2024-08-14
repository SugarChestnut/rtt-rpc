package cn.rentaotao.jdk.net.bio;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;

/**
 * @author rtt
 * @date 2024/8/14 14:22
 */
public class BioClient {

    private Socket client;

    public void connect(String host, int port) throws Exception{
        client = new Socket();
        client.connect(new InetSocketAddress(host, port));
        while (!client.isConnected()) {}
        SocketChannel channel = client.getChannel();
    }

    public static void main(String[] args) {

    }
}
