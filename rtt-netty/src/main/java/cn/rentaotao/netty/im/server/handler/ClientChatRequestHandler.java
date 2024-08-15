package cn.rentaotao.netty.im.server.handler;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.concurrent.ExecuteTask;
import cn.rentaotao.netty.im.concurrent.FutureTaskScheduler;
import cn.rentaotao.netty.im.server.ServerSession;
import cn.rentaotao.netty.im.server.processor.ChatRedirectProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author rtt
 * @create 2021/3/31 15:33
 */
public class ClientChatRequestHandler extends ChannelInboundHandlerAdapter {

    private final ChatRedirectProcessor chatRedirectProcessor = new ChatRedirectProcessor();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 判断消息内容
        if (!(msg instanceof ImOuterClass.Im.Message)) {
            super.channelRead(ctx, msg);
            return;
        }

        ImOuterClass.Im.Message message = (ImOuterClass.Im.Message) msg;

        // 判断消息类型
        if (chatRedirectProcessor.type() != message.getHeadType()) {
            super.channelRead(ctx, msg);
            return;
        }

        // 获取当前用户的 session 信息
        ServerSession serverSession = ctx.channel().attr(ServerSession.SESSION_KEY).get();

        if (serverSession == null || !serverSession.isLogin()) {
            System.out.println("用户尚未登录，不能发送信息");
            return;
        }

        FutureTaskScheduler.add(new ExecuteTask() {
            @Override
            public void execute() {
                chatRedirectProcessor.action(serverSession, message);
            }
        });
    }
}
