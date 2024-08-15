package cn.rentaotao.netty.im.client.handler;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.client.ClientSession;
import cn.rentaotao.netty.im.client.builder.HeartBeatBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @create 2021/4/2 09:58
 */
public class HeartBeatClientHandler extends ChannelInboundHandlerAdapter {

    private static final int HEATBEAT_INTERVAL = 10;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 在处理器被加入到流水线的时候触发
        ClientSession session = ClientSession.getSession(ctx);
        ImOuterClass.Im.Message message = new HeartBeatBuilder(session).build();
        heartBeat(ctx, message);
    }

    private void heartBeat(ChannelHandlerContext ctx, ImOuterClass.Im.Message message) {
        ctx.executor().schedule(new Runnable() {
            @Override
            public void run() {
                if (ctx.channel().isActive()) {
                    System.out.println("发送 HEART_BEAT 消息");
                    ctx.writeAndFlush(message);

                    heartBeat(ctx, message);
                }
            }
        }, HEATBEAT_INTERVAL, TimeUnit.SECONDS);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg.getClass());
        if (!(msg instanceof ImOuterClass.Im.Message)) {
            super.channelRead(ctx, msg);
            return;
        }
        ImOuterClass.Im.Message message = (ImOuterClass.Im.Message) msg;
        ImOuterClass.Im.HeadType headType = message.getHeadType();
        System.out.println(headType.name());
        if (headType.equals(ImOuterClass.Im.HeadType.MESSAGE_KEEPALIVE)) {
            System.out.println("收到服务器回复 HEART_BEAT");
        } else {
            super.channelRead(ctx, msg);
        }
    }
}
