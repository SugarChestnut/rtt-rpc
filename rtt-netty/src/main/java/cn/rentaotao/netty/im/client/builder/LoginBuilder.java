package cn.rentaotao.netty.im.client.builder;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.bean.User;
import cn.rentaotao.netty.im.client.ClientSession;

/**
 * @author rtt
 * @create 2021/3/30 10:17
 */
public class LoginBuilder extends BaseBuilder{

    public LoginBuilder(ClientSession session) {
        super(ImOuterClass.Im.HeadType.LOGIN_REQUEST, session);
    }

    @Override
    public ImOuterClass.Im.Message build() {
        User user = session.getUser();
        ImOuterClass.Im.Message message = buildCommon(-1);
        ImOuterClass.Im.LoginRequest.Builder builder = ImOuterClass.Im.LoginRequest
                .newBuilder()
                .setDeviceId(user.getDevId())
                .setPlatform(user.getPlatform().ordinal())
                .setUid(user.getUid())
                .setToken(user.getToken());

        return message.toBuilder().setLoginRequest(builder).build();
    }
}
