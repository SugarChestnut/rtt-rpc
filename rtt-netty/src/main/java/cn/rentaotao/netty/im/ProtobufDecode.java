package cn.rentaotao.netty.im;

import cn.rentaotao.netty.im.bean.ImOuterClass;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author rtt
 * @create 2021/3/26 09:41
 */
public class ProtobufDecode extends ByteToMessageDecoder {

    static final int MAGIC_LENGTH = 2;

    static final int VERSION_LENGTH = 2;

    static final int CONTENT_LENGTH = 2;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 保存读的位置
        in.markReaderIndex();
        // 判断可读字节长度
        if (in.readableBytes() < MAGIC_LENGTH) {
            return;
        }

        short magic = in.readShort();
        // 如果魔数校验错误，关闭连接
        if (magic != 1) {
            ctx.channel();
        }

        // 继续判断可读长度
        if (in.readableBytes() < VERSION_LENGTH) {
            // 重置读索引
            in.resetReaderIndex();
            return;
        }

        short version = in.readShort();

        // 读取内容长度
        if (in.readableBytes() < CONTENT_LENGTH) {
            in.resetReaderIndex();
            return;
        }

        short length = in.readShort();

        if (length < 0) {
            // TODO 这里的 close 关闭的是什么
            ctx.close();
            return;
        }
        if (in.readableBytes() < length) {
            in.resetReaderIndex();
            return;
        }
        byte[] array = new byte[length];

        in.readBytes(array, 0, length);

        // 字节转换

        ImOuterClass.Im.Message message = ImOuterClass.Im.Message.parseFrom(array);
        if (message != null) {
            out.add(message);
        }
    }
}
