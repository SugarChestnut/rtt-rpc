package cn.rentaotao.netty.im.server.processor;


import cn.rentaotao.netty.im.ProtoInstant;
import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.bean.User;
import cn.rentaotao.netty.im.server.ServerSession;
import cn.rentaotao.netty.im.server.SessionMap;
import cn.rentaotao.netty.im.server.builder.LoginResponseBuilder;

/**
 * @author rtt
 * @create 2021/3/31 14:57
 */
public class LoginProcessor extends AbstractServerProcessor{

    private final LoginResponseBuilder loginResponseBuilder = new LoginResponseBuilder();

    @Override
    public ImOuterClass.Im.HeadType type() {
        return ImOuterClass.Im.HeadType.LOGIN_REQUEST;
    }

    @Override
    public boolean action(ServerSession session, ImOuterClass.Im.Message message) {

        long sequence = message.getSequence();
        // 获取登录信息
        ImOuterClass.Im.LoginRequest request = message.getLoginRequest();
        // 获取用户信息
        User user = User.fromMsg(request);
        // 校验用户
        if(!checkUser(user)) {
            // 返回失败响应
            session.writeAndFlush(loginResponseBuilder
                    .builder(ProtoInstant.ResultCodeEnum.NO_TOKEN, sequence, "-1"));

            return false;
        }
        // 绑定用户
        session.setUser(user);
        // 进行双向绑定
        session.bind();
        // 返回成功响应
        session.writeAndFlush(loginResponseBuilder
                .builder(ProtoInstant.ResultCodeEnum.SUCCESS, sequence, session.getSessionId()));

        return true;
    }

    private boolean checkUser(User user) {
        if (SessionMap.inst().hasLogin(user)) {
            return false;
        }
        // 其他校验用户的服务
        return true;
    }
}
