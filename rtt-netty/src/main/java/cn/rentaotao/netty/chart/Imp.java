package cn.rentaotao.netty.chart;

import lombok.Getter;

/**
 * @author rtt
 * @date 2023/2/17 14:41
 */
@Getter
public enum Imp {

    /**
     *
     */
    SYSTEM("system"),
    /**
     *
     */
    LOGIN("login"),
    /**
     *
     */
    LOGOUT("logout"),
    /**
     *
     */
    CHAT("chat"),
    /**
     *
     */
    FLOWER("flower");

    private final String type;

    Imp(String type) {
        this.type = type;
    }

    public static boolean isImp(String content) {
        return content.matches("(system|login|logout|chat|flower)");
    }
}
