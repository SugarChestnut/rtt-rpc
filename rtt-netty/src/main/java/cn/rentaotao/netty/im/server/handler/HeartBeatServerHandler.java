package cn.rentaotao.netty.im.server.handler;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.concurrent.FutureTaskScheduler;
import cn.rentaotao.netty.im.server.ServerSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @create 2021/4/2 09:41
 */
public class HeartBeatServerHandler extends IdleStateHandler {

    /**
     * 最大空闲，单位 s
     */
    private static final int READ_IDLE_GAP = 150;

    public HeartBeatServerHandler() {
        // 入站空闲时长   出站空闲时长  出入站检测时长
        super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ImOuterClass.Im.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ImOuterClass.Im.Message message = (ImOuterClass.Im.Message) msg;
        ImOuterClass.Im.HeadType headType = message.getHeadType();;
        if (headType.equals(ImOuterClass.Im.HeadType.MESSAGE_KEEPALIVE)) {
            System.out.println("收到客户端心跳消息");
            FutureTaskScheduler.add(() -> {
                if (ctx.channel().isActive()) {
                    ctx.writeAndFlush(msg);
                }
            });
        }
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        System.out.println(READ_IDLE_GAP + "秒未读到数据，关闭连接");
        //  TODO 同时用户要退出登录
        if (ctx.channel().isActive()) {
            ctx.close();
            ServerSession.closeSession(ctx.channel());
        }
    }
}
