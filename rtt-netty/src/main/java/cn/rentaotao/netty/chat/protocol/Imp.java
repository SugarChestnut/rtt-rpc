package cn.rentaotao.netty.chat.protocol;

/**
 * @author rtt
 * @date 2023/2/17 14:41
 */
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

    public String getType() {
        return type;
    }
}
