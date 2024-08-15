package cn.rentaotao.netty.im.server;

import cn.rentaotao.netty.im.bean.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.AttributeKey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author rtt
 * @create 2021/3/31 13:56
 */
public class ServerSession {

    public static final AttributeKey<String> KEY_USER_ID = AttributeKey.valueOf("key_user_id");

    public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("session_key");

    /**
     * 连接通道
     */
    private final Channel channel;

    /**
     * 用户登录session值
     */
    private final String sessionId;

    /**
     * 用户
     */
    private User user;

    /**
     * 登录状态
     */
    private boolean isLogin = false;

    /**
     * 存储属性值
     */
    private final Map<String, Object> map = new HashMap<>(8);

    public ServerSession(Channel channel) {
        this.channel = channel;
        this.sessionId = buildSessionId();
    }

    /**
     * 获取通道的 session
     *
     * @param channel 通道
     * @return session
     */
    public static ServerSession getServerSession(Channel channel) {
        return channel.attr(ServerSession.SESSION_KEY).get();
    }

    /**
     * 关闭 session
     *
     * @param channel 通道
     */
    public static void closeSession(Channel channel) {
        ServerSession serverSession = getServerSession(channel);
        if (serverSession != null && serverSession.isValid()) {
            serverSession.close();
            SessionMap.inst().removeSession(serverSession.getSessionId());
        }
    }

    /**
     * 用户登录后，进行信息绑定
     *
     * @return
     */
    public ServerSession bind() {
        System.out.println("ServerSession 绑定会话：" + channel.remoteAddress());
        // 将 session 绑定到 channel
        channel.attr(ServerSession.SESSION_KEY).set(this);
        // 将 session 添加到容器中
        SessionMap.inst().addSession(this);
        // 登录成功的状态
        isLogin = true;
        return this;
    }

    /**
     * 解绑
     *
     * @return
     */
    public ServerSession unbind() {
        isLogin = false;
        SessionMap.inst().removeSession(sessionId);
        this.close();
        return this;
    }

    /**
     * 在 session 上保存数据
     *
     * @param key
     * @param value
     */
    public synchronized void set(String key, Object value) {
        map.put(key, value);
    }

    /**
     * 获得属性值
     *
     * @param key
     * @return
     */
    public synchronized Object get(String key) {
        return map.get(key);
    }

    public boolean isValid() {
        return getUser() != null;
    }

    /**
     * 向通道写数据
     *
     * @param msg 数据
     */
    public synchronized void writeAndFlush(Object msg) {
        channel.writeAndFlush(msg);
    }

    /**
     * 关闭客户端连接
     */
    public synchronized void close() {
        ChannelFuture future = channel.close();
        future.addListener((ChannelFuture f) -> {
            if (!f.isSuccess()) {
                System.out.println("通道关闭失败");
            }
        });
    }

    public void setUser(User user) {
        this.user = user;
        user.setSessionId(sessionId);
    }

    public User getUser() {
        return user;
    }

    private String buildSessionId() {
        String str = UUID.randomUUID().toString();
        return str.replaceAll("-", "");
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isLogin() {
        return isLogin;
    }
}
