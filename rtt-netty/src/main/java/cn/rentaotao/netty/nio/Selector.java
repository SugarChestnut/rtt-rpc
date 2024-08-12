package cn.rentaotao.netty.nio;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;

/**
 * @author rtt
 * @date 2023/1/31 15:48
 */
public class Selector {

    public static void main(String[] args) throws IOException {
        try (java.nio.channels.Selector selector = java.nio.channels.Selector.open()) {
        }

        ServerSocketChannel.open();


    }
}
