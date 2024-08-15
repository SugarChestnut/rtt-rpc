package cn.rentaotao.netty.im.client.builder;

import cn.rentaotao.netty.im.bean.ChatMsg;
import cn.rentaotao.netty.im.bean.ImOuterClass;
import cn.rentaotao.netty.im.client.ClientSession;

/**
 * @author rtt
 * @create 2021/3/30 17:03
 */
public class ChatBuilder extends BaseBuilder{

    private String to;

    private String content;

    public ChatBuilder(ClientSession session, String to, String content) {
        super(ImOuterClass.Im.HeadType.MESSAGE_REQUEST, session);
        this.to = to;
        this.content = content;
    }


    @Override
    public ImOuterClass.Im.Message build() {
        ImOuterClass.Im.MessageRequest.Builder builder = ImOuterClass.Im.MessageRequest.newBuilder();

        ChatMsg chatMsg = new ChatMsg();
        chatMsg.setMsgId(System.currentTimeMillis());
        chatMsg.setFrom(session.getUser().getUid());
        chatMsg.setTo(to);
        chatMsg.setContent(content);
        chatMsg.setMsgType(ChatMsg.MsgType.TEXT);
        chatMsg.setTime(System.currentTimeMillis());
        chatMsg.setFromNick(session.getUser().getNickName());

        chatMsg.fillMsg(builder);

        ImOuterClass.Im.Message message = buildCommon(-1);

        return message.toBuilder().setMessageRequest(builder.build()).build();
    }
}
