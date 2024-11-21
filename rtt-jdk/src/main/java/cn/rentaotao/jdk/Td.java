package cn.rentaotao.jdk;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @date 2024/9/2 09:49
 */
public class Td {

    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<Object> blockingQueue = new ArrayBlockingQueue<>(1);

        Object poll = blockingQueue.poll(5L, TimeUnit.SECONDS);

        System.out.println(poll);
    }
}
