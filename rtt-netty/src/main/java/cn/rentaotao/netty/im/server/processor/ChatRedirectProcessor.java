package cn.rentaotao.netty.im.server.processor;


import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.server.ServerSession;
import cn.rentaotao.netty.im.server.SessionMap;

import java.util.List;

/**
 * @author rtt
 * @create 2021/3/31 15:35
 */
public class ChatRedirectProcessor extends AbstractServerProcessor{

    @Override
    public ImOuterClass.Im.HeadType type() {
        return ImOuterClass.Im.HeadType.MESSAGE_REQUEST;
    }

    @Override
    public boolean action(ServerSession session, ImOuterClass.Im.Message message) {

        ImOuterClass.Im.MessageRequest messageRequest = message.getMessageRequest();

        System.out.println("chatMsg | from=" + messageRequest.getForm() + " | to=" + messageRequest.getTo() + " | content=" + messageRequest.getContent());

        String to = messageRequest.getTo();

        List<ServerSession> serverSessionList = SessionMap.inst().getSessionByUserId(to);

        if (serverSessionList == null || serverSessionList.size() < 1) {
            // 用户没有登录
            System.out.println("[" + to + "] 不在线，发送失败" );
            return false;
        }

        serverSessionList.forEach(s -> s.writeAndFlush(message));

        return true;
    }
}
