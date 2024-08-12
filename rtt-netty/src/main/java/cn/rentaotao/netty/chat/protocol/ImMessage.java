package cn.rentaotao.netty.chat.protocol;


/**
 * @author rtt
 * @date 2023/2/17 14:51
 */
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

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    @Override
    public String toString() {
        return "ImMessage{" +
                "addr='" + addr + '\'' +
                ", cmd='" + cmd + '\'' +
                ", time=" + time +
                ", online=" + online +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", terminal='" + terminal + '\'' +
                '}';
    }
}
