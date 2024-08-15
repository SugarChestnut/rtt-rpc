package cn.rentaotao.netty.im.server.handler;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.concurrent.CallbackTask;
import cn.rentaotao.netty.im.concurrent.CallbackTaskScheduler;
import cn.rentaotao.netty.im.server.ServerSession;
import cn.rentaotao.netty.im.server.processor.LoginProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author rtt
 * @create 2021/3/30 20:22
 */
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {

    private final LoginProcessor loginProcessor = new LoginProcessor();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("收到一个新连接，但没有登录{ " + ctx.channel().id() + " }");
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (!(msg instanceof ImOuterClass.Im.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ImOuterClass.Im.Message message = (ImOuterClass.Im.Message) msg;

        // 获取请求类型
        ImOuterClass.Im.HeadType headType = message.getHeadType();

        if (loginProcessor.type() != headType) {
            super.channelRead(ctx, msg);
            return;
        }

        ServerSession serverSession = new ServerSession(ctx.channel());

        CallbackTaskScheduler.add(new CallbackTask<Boolean>() {
            @Override
            public Boolean execute() throws Exception {
                return loginProcessor.action(serverSession, message);
            }

            @Override
            public void onBack(Boolean r) {
                if (r) {
                    ctx.pipeline().addAfter("login", "chat", new ClientChatRequestHandler());
                    ctx.pipeline().remove("login");
                    System.out.println("登录成功：" + serverSession.getUser().toString());
                } else {
                    ServerSession.closeSession(ctx.channel());
                    System.out.println("登录失败：" + serverSession.getUser().toString());
                }
            }

            @Override
            public void onException(Throwable t) {
                ServerSession.closeSession(ctx.channel());
                System.out.println("登录失败：" + serverSession.getUser().toString());
            }
        });
    }
}
