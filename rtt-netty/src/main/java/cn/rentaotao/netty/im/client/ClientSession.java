package cn.rentaotao.netty.im.client;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.bean.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @author rtt
 * @create 2021/3/30 09:35
 */
public class ClientSession {

    public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

    private final Channel channel;

    private User user;

    private String sessionId;

    private boolean isConnected = false;

    private boolean isLogin = false;

    public ClientSession(Channel channel) {
        this.channel = channel;
        this.sessionId = String.valueOf(-1);
        // 在通道上绑定会话信息
        channel.attr(ClientSession.SESSION_KEY).set(this);
    }

    public static void loginSuccess (ChannelHandlerContext ctx, ImOuterClass.Im.Message message) {
        Channel channel = ctx.channel();
        ClientSession clientSession = channel.attr(ClientSession.SESSION_KEY).get();
        clientSession.setLogin(true);
        clientSession.setSessionId(message.getSessionId());
        System.out.println("登录成功");
    }

    public static ClientSession getSession(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.attr(ClientSession.SESSION_KEY).get();
    }

    public String getRemoteAddress() {
        return channel.remoteAddress().toString();
    }

    /**
     * 向通道写入数据
     * @param obj 数据
     * @return 异步任务
     */
    public ChannelFuture writeAndFlush(Object obj) {
        return channel.writeAndFlush(obj);
    }

    public void writeAndClose(Object obj){
        ChannelFuture channelFuture = channel.writeAndFlush(obj);
        channelFuture.addListener(ChannelFutureListener.CLOSE);
    }

    public void close() {
        isConnected = false;
        ChannelFuture future = channel.close();
        future.addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                System.out.println("连接断开成功");
            }
        });
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public User getUser() {
        return user;
    }

    public void setUser(String username, String password) {

        User user = new User();
        user.setUid(username);
        user.setToken(password);
        user.setDevId("111");

        this.user = user;
    }

    public void removeUser() {
        this.user = null;
    }

    public Channel getChannel() {
        return channel;
    }
}
