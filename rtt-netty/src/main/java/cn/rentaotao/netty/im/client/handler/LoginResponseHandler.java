package cn.rentaotao.netty.im.client.handler;

import cn.rentaotao.netty.im.ProtoInstant;
import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.client.ClientSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * @author rtt
 * @create 2021/3/30 11:46
 */
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 响应为空
        if (!(msg instanceof ImOuterClass.Im.Message)) {
            // 传递
            super.channelRead(ctx, msg);
            return;
        }

        // 获取响应类型
        ImOuterClass.Im.Message message = (ImOuterClass.Im.Message) msg;
        ImOuterClass.Im.HeadType headType = message.getHeadType();

        // 响应类型非登录响应
        if (!ImOuterClass.Im.HeadType.LOGIN_RESPONSE.equals(headType)) {
            // 传递
            super.channelRead(ctx, msg);
            return;
        }

        ImOuterClass.Im.LoginResponse loginResponse = message.getLoginResponse();
        // 有点取巧了
        ProtoInstant.ResultCodeEnum result = ProtoInstant.ResultCodeEnum.values()[loginResponse.getCode()];

        if (ProtoInstant.ResultCodeEnum.SUCCESS.getCode().equals(result.getCode())) {
            // 登录成功
            ClientSession.loginSuccess(ctx, message);
            ChannelPipeline pipeline = ctx.pipeline();
            // 登录成功后移除处理器
            pipeline.remove(this);
            pipeline.addAfter("decoder", "heartbeat", new HeartBeatClientHandler());
        } else {
            // 登录失败
            System.out.println("登录失败：" + result.getDesc());
            ClientSession clientSession = ctx.channel().attr(ClientSession.SESSION_KEY).get();
            if (clientSession != null) {
                clientSession.removeUser();
            }
        }
    }
}
