package cn.rentaotao.netty.protocol.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.nio.charset.StandardCharsets;

/**
 * @author rtt
 * @date 2023/2/1 14:26
 */
public class RttHttpResponse {

    private final ChannelHandlerContext ctx;

    public RttHttpResponse(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public void write(String str) {
        if (null == str) {
            str = "";
        }

        final DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(str.getBytes(StandardCharsets.UTF_8))
        );

        response.headers().set("Content-Type", "text/html");

        ctx.write(response);

        ctx.flush();

        ctx.close();
    }
}
