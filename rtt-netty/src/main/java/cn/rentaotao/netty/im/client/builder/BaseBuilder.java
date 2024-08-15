package cn.rentaotao.netty.im.client.builder;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.client.ClientSession;

/**
 * @author rtt
 * @create 2021/3/30 10:17
 */
public abstract class BaseBuilder {

    protected ImOuterClass.Im.HeadType headType;

    long seqId;

    ClientSession session;

    BaseBuilder(ImOuterClass.Im.HeadType headType, ClientSession session) {
        this.headType = headType;
        this.session = session;
    }

    ImOuterClass.Im.Message buildCommon(long seqId) {
        this.seqId = seqId;

        ImOuterClass.Im.Message.Builder builder = ImOuterClass.Im.Message
                .newBuilder()
                .setHeadType(headType)
                .setSessionId(session.getSessionId())
                .setSequence(seqId);

        return builder.buildPartial();
    }

    /**
     * 通过该方法构建信息
     *
     * @return 信息
     */
    public abstract ImOuterClass.Im.Message build();
}
