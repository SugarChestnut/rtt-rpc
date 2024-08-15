package cn.rentaotao.netty.im.client.builder;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.bean.User;
import cn.rentaotao.netty.im.client.ClientSession;

/**
 * @author rtt
 * @create 2021/4/2 10:14
 */
public class HeartBeatBuilder extends BaseBuilder{

    public HeartBeatBuilder(ClientSession session) {
        super(ImOuterClass.Im.HeadType.MESSAGE_KEEPALIVE, session);
    }
    @Override
    public ImOuterClass.Im.Message build() {
        ImOuterClass.Im.Message message = buildCommon(-1);
        User user = session.getUser();
        ImOuterClass.Im.MessageKeepalive.Builder builder = ImOuterClass.Im.MessageKeepalive.newBuilder()
                .setSeq(0)
                .setJson("{\"from\":\"client\"}")
                .setUid(user.getUid());
        return message.toBuilder().setMessageKeepalive(builder).build();
    }
}
