package cn.rentaotao.common.utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author rtt
 * @create 2021/3/29 10:50
 */
public class RandomUtils {

    private static final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();

    public static int getRandomInt(int mod) {
        return threadLocalRandom.nextInt(mod);
    }
}
