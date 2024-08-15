package cn.rentaotao.netty.im.server.builder;


import cn.rentaotao.netty.im.ProtoInstant;
import cn.rentaotao.netty.im.bean.ImOuterClass;

/**
 * @author rtt
 * @create 2021/3/31 13:44
 */
public class LoginResponseBuilder {

    public ImOuterClass.Im.Message builder(ProtoInstant.ResultCodeEnum code, long seq, String session) {
        ImOuterClass.Im.Message.Builder mb = ImOuterClass.Im.Message.newBuilder()
                .setHeadType(ImOuterClass.Im.HeadType.LOGIN_RESPONSE)
                .setSequence(seq)
                .setSessionId(session);
        ImOuterClass.Im.LoginResponse.Builder lb = ImOuterClass.Im.LoginResponse.newBuilder()
                .setCode(code.getCode())
                .setInfo(code.getDesc())
                .setExpose(1);

        return mb.setLoginResponse(lb.build()).build();
    }
}
