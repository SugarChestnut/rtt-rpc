package cn.rentaotao.netty.chart.server;

import cn.rentaotao.netty.chart.protocol.ImMessage;
import cn.rentaotao.netty.chart.protocol.Imp;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author rtt
 * @date 2023/2/17 16:51
 */
public class MsgProcessor {

    /**
     * 记录在线用户
     */
    private static final ChannelGroup ONLINE_USERS = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static final AttributeKey<String> NICK_NAME = AttributeKey.valueOf("nickName");
    public static final AttributeKey<String> IP_ADDR = AttributeKey.valueOf("ipAddr");
    public static final AttributeKey<JSONObject> ATTRS = AttributeKey.valueOf("attrs");
    public static final AttributeKey<String> FROM = AttributeKey.valueOf("from");

    public String getNickName(Channel client) {
        return client.attr(NICK_NAME).get();
    }

    public String getAddress(Channel client) {
        return client.remoteAddress().toString().replaceFirst("/", "");
    }

    public JSONObject getAttrs(Channel client) {
        try {
            return client.attr(ATTRS).get();
        } catch (Exception e) {
            return null;
        }
    }

    public void setAttrs(Channel client, String key, Object value) {
        try {
            JSONObject attrs = getAttrs(client);
            attrs.put(key, value);
            client.attr(ATTRS).set(attrs);
        } catch (Exception e) {
            JSONObject o = new JSONObject();
            o.put(key, value);
            client.attr(ATTRS).set(o);
        }
    }

    private void logout(Channel client) {
        if (getNickName(client) == null) {
            return;
        }
        for (Channel channel : ONLINE_USERS) {
            if (channel == client) {
                continue;
            }
            ImMessage imMessage = new ImMessage().createSystemMsg(getNickName(client) + "离开", ONLINE_USERS.size());
            String content = JSON.toJSONString(imMessage);
            channel.writeAndFlush(new TextWebSocketFrame(content));
        }
        ONLINE_USERS.remove(client);
    }

    private void login(Channel client, ImMessage msg) {
        client.attr(NICK_NAME).getAndSet(msg.getSender());
        client.attr(IP_ADDR).getAndSet(getAddress(client));
        client.attr(FROM).getAndSet(msg.getTerminal());
        ONLINE_USERS.add(client);

        for (Channel channel : ONLINE_USERS) {
            boolean isSelf = channel == client;
            if (isSelf) {
                msg = new ImMessage().createSystemMsg(getNickName(client) + "已经与服务器建立连接", ONLINE_USERS.size());
            } else {
                msg = new ImMessage().createSystemMsg(getNickName(client) + "加入", ONLINE_USERS.size());
            }

            // 判断连接的来源
            if ("Console".equals(channel.attr(FROM).get())) {
                channel.writeAndFlush(msg);
            } else {
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
            }
        }
    }

    private void chat(Channel client, ImMessage msg) {
        for (Channel channel : ONLINE_USERS) {
            ImMessage m;
            boolean isSelf = channel == client;
            if (isSelf) {
                m = new ImMessage().createChatMsg("You", getNickName(channel), msg.getContent());
            } else {
                m = new ImMessage().createChatMsg(getNickName(client), getNickName(channel), msg.getContent());
            }

            if ("Console".equals(channel.attr(FROM).get())) {
                channel.writeAndFlush(m);
            } else {
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(m)));
            }
        }
    }

    private void flower(Channel client) {
        JSONObject attrs = getAttrs(client);
        long currTime = sysTime();

        if (attrs != null) {
            Long lastTime = attrs.getLong("lastFlowerTime");
            // 10s 内不能重复送鲜花
            if ((currTime - lastTime) < 10 * 1000) {
                ImMessage msg = new ImMessage().createSystemMsg("太频繁了，稍后再试", ONLINE_USERS.size());
                client.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
                return;
            }
        }

        for (Channel channel : ONLINE_USERS) {
            ImMessage msg = new ImMessage().createFlowerMsg(getNickName(client));
            setAttrs(client, "lastFlowerTime", currTime);
            channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(msg)));
        }
    }

    public void sendMsg(Channel client, String content) {
        sendMsg(client, JSON.parseObject(content, ImMessage.class));
    }

    public void sendMsg(Channel client, ImMessage msg) {
        if (msg == null) {
            return;
        }

        // 登陆
        if (Imp.LOGIN.getType().equals(msg.getCmd())) {
            login(client, msg);
        }

        // 登出
        if (Imp.LOGOUT.getType().equals(msg.getCmd())) {
            logout(client);
        }

        // 聊天
        if (Imp.CHAT.getType().equals(msg.getCmd())) {
            chat(client, msg);
        }

        // Flower
        if (Imp.FLOWER.getType().equals(msg.getCmd())) {
            flower(client);
        }
    }

    private long sysTime() {
        return System.currentTimeMillis();
    }

}
