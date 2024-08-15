package cn.rentaotao.netty.chart;


import lombok.Data;

/**
 * @author rtt
 * @date 2023/2/17 14:51
 */
@Data
public class ImMessage {

    private String addr;
    private String cmd;
    private long time;
    private int online;
    private String sender;
    private String receiver;
    private String content;
    private String terminal;

    public ImMessage createFlowerMsg(String sender) {
        this.cmd = Imp.FLOWER.getType();
        this.time = System.currentTimeMillis();
        this.sender = sender;

        return this;
    }

    public ImMessage createSystemMsg(String content, int online) {
        this.cmd = Imp.SYSTEM.getType();
        this.time = System.currentTimeMillis();
        this.content = content;
        this.online = online;

        return this;
    }

    public ImMessage createChatMsg(String sender, String receiver, String content) {
        this.cmd = Imp.CHAT.getType();
        this.time = System.currentTimeMillis();
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;

        return this;
    }

    public ImMessage createLoginMsg(String sender) {
        this.cmd = Imp.LOGIN.getType();
        this.time = System.currentTimeMillis();
        this.sender = sender;
        this.terminal = "Console";

        return this;
    }

    public ImMessage createLogoutMsg(String sender) {
        this.cmd = Imp.LOGOUT.getType();
        this.time = System.currentTimeMillis();
        this.sender = sender;
        this.terminal = "Console";

        return this;
    }
}
