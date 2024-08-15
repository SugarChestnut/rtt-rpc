package cn.rentaotao.netty.chart.server;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * 处理Http请求
 *
 * @author rtt
 * @date 2023/2/17 16:11
 */
public class HttpRequestInboundHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final URL baseUrl = HttpRequestInboundHandler.class.getResource("");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String uri = msg.uri();
        String page = "/".equals(uri) ? "chat.html" : uri;
        try (RandomAccessFile file = new RandomAccessFile(getResource(page), "r")) {
            DefaultHttpResponse response = new DefaultHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
            String contentType = "text/html;";
            if (uri.endsWith(".css")) {
                contentType = "text/css;";
            } else if (uri.endsWith(".js;")) {
                contentType = "text/javascript";
            } else if (uri.toLowerCase().matches(".*\\.(jpg|png|gif)$")) {
                contentType = "image/" + uri.substring(uri.lastIndexOf(".")) + ";";
            }
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType + "charset=utf-8");
            boolean keepAlive = HttpUtil.isKeepAlive(msg);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);
            ctx.write(new DefaultFileRegion(file.getChannel(), 0, file.length()));
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        } catch (FileNotFoundException f) {
            ctx.write(new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.NOT_FOUND));
            ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            ctx.close();
        } catch (Exception e) {
            ctx.fireChannelRead(msg.retain());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        System.out.println("Client:" + channel.remoteAddress() + "异常");
        cause.printStackTrace();
        ctx.close();
    }

    private File getResource(String fileName) throws Exception {
        assert baseUrl != null;
        String basePath = baseUrl.toURI().toString();
        int start = basePath.indexOf("classes/");
        basePath = (basePath.substring(0, start) + "/classes/").replaceAll("/+", "/");
        String path;
        if (fileName.endsWith("html")) {
            path = basePath + "template/" + fileName;
        } else {
            path = basePath + fileName;
        }
        path = path.contains("file:") ? path.substring(5) : path;
        path = path.replaceAll("//", "/");
        return new File(path);
    }
}
