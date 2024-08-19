package cn.rentaotao.netty.chart.client;

import cn.rentaotao.netty.chart.ImMessage;
import cn.rentaotao.netty.chart.Imp;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author rtt
 * @date 2023/2/20 14:05
 */
public class ChatClientHandler extends SimpleChannelInboundHandler<ImMessage> {

    private ChannelHandlerContext ctx;

    private final String nickName;

    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }

    private void session() {
        new Thread(() -> {
            System.out.println(nickName + "，你好，请在控制台输入对话内容");
            ImMessage msg = null;
            Scanner scanner = new Scanner(System.in);
            do {
                if (scanner.hasNext()) {
                    String l = scanner.nextLine();
                    if ("exit".equals(l)) {
                        msg = new ImMessage().createLogoutMsg(nickName);
                    } else {
                        msg = new ImMessage().createChatMsg(nickName, "All", l);
                    }
                }
            } while (sendMsg(msg));
            scanner.close();
        }).start();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        ImMessage msg = new ImMessage().createLoginMsg(nickName);
        sendMsg(msg);
        System.out.println("成功连接服务器，已执行登录动作");
        session();
    }

    private boolean sendMsg(ImMessage msg) {
        ctx.channel().writeAndFlush(msg);
        return !Imp.LOGOUT.getType().equals(msg.getCmd());
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        if (Imp.SYSTEM.getType().equals(msg.getCmd())) {
            System.out.println("[" + msg.getCmd() + "][" + msg.getTime() + "] - " + msg.getContent());
        } else {
            System.out.println("[" + msg.getCmd() + "][" + msg.getTime() + "][" + msg.getSender() + "] - " + msg.getContent());
        }
    }

    private String removeHtmlTag(String  str) {
        String regExScript = "<script[^>]*?>[\\s\\S]*?</script>";
        String regExStyle = "<style[^>]*?>[\\s\\S]*?</style>";
        String regExHtml = "<[^>]+>";

        Pattern pScript = Pattern.compile(regExScript, Pattern.CASE_INSENSITIVE);
        Matcher mScript = pScript.matcher(str);
        str = mScript.replaceAll("");

        Pattern pStyle = Pattern.compile(regExStyle, Pattern.CASE_INSENSITIVE);
        Matcher mStyle = pStyle.matcher(str);
        str = mStyle.replaceAll("");

        Pattern pHtml = Pattern.compile(regExHtml, Pattern.CASE_INSENSITIVE);
        Matcher mHtml = pHtml.matcher(str);
        str = mHtml.replaceAll("");

        return str.trim();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("与服务器断开连接：" + cause.getMessage());
        ctx.close();
    }
}
