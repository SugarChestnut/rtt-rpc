package cn.rentaotao.netty.im.server.processor;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.server.ServerSession;
import io.netty.channel.Channel;

/**
 * @author rtt
 * @create 2021/3/31 13:54
 */
public abstract class AbstractServerProcessor {

    protected String getKey(Channel channel) {
        return channel.attr(ServerSession.KEY_USER_ID).get();
    }

    protected void setKey(Channel channel, String key) {
        channel.attr(ServerSession.KEY_USER_ID).set(key);
    }

    protected void auth(Channel channel) throws Exception {
        if (getKey(channel) == null) {
            throw new Exception("此用户没有登录成功");
        }
    }

    public abstract ImOuterClass.Im.HeadType type();

    public abstract boolean action(ServerSession session, ImOuterClass.Im.Message message);
}
