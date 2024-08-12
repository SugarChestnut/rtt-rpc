package cn.rentaotao.netty.aio;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author rtt
 * @date 2023/1/31 13:46
 */
public class AioServer {

    public static void main(String[] args) {

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            list.add(i + "");
        }

        Iterator<String> iterator = list.iterator();

        while (iterator.hasNext()) {
            System.out.println(iterator.next());
            iterator.remove();
        }

        System.out.println(list.size());
    }
}
