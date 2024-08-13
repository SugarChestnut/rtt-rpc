package cn.rentaotao.jdk.buf;

import java.nio.ByteBuffer;

/**
 * @author rtt
 * @date 2024/8/13 16:14
 */
public class DirectBuffer {

    public static void main(String[] args) {
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);
    }
}
