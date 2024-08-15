package cn.rentaotao.netty.im.bean;

import io.netty.util.internal.StringUtil;

/**
 * @author rtt
 * @create 2021/3/31 20:20
 */
public class ChatMsg {

    private long msgId;
    private String from;
    private String to;
    private long time;
    private MsgType msgType;
    private String content;
    private String url;
    private String property;
    private String fromNick;
    private String json;

    public void fillMsg(ImOuterClass.Im.MessageRequest.Builder builder) {

        if (msgId > 0) {
            builder.setMsgId(msgId);
        }
        if (!StringUtil.isNullOrEmpty(from)) {
            builder.setForm(from);
        }
        if (!StringUtil.isNullOrEmpty(to)) {
            builder.setTo(to);
        }
        if (time > 0) {
            builder.setTime(time);
        }
        if (msgType != null) {
            builder.setMsgType(msgType.ordinal());
        }
        if (!StringUtil.isNullOrEmpty(content)) {
            builder.setContent(content);
        }
        if (!StringUtil.isNullOrEmpty(url)) {
            builder.setUrl(url);
        }
        if (!StringUtil.isNullOrEmpty(property)) {
            builder.setProperty(property);
        }
        if (!StringUtil.isNullOrEmpty(fromNick)) {
            builder.setFromNick(fromNick);
        }
        if (!StringUtil.isNullOrEmpty(json)) {
            builder.setJson(json);
        }
    }

    public enum MsgType {
        /**
         * 文本
         */
        TEXT,
        /**
         * 视频
         */
        AUDIO,
        /**
         * 音频
         */
        VIDEO,
        /**
         * 位置
         */
        POS,
        /**
         * 其他
         */
        OTHER
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getFromNick() {
        return fromNick;
    }

    public void setFromNick(String fromNick) {
        this.fromNick = fromNick;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
