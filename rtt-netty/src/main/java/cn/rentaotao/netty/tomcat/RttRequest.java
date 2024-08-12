package cn.rentaotao.netty.tomcat;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.Map;

/**
 * @author rtt
 * @date 2023/2/1 14:26
 */
public class RttRequest {

    private final HttpRequest request;

    public RttRequest(HttpRequest request) {
        this.request = request;
    }

    public String getUrl() {
        return this.request.uri();
    }

    public String getMethod() {
        return request.method().name();
    }

    public Map<String, List<String>> getParameters() {
        return new QueryStringDecoder(request.uri()).parameters();
    }

    public String getParameter(String name) {
        final Map<String, List<String>> parameters = getParameters();
        final List<String> params = parameters.get(name);
        if (null == params) {
            return null;
        }
        return params.get(0);
    }
}
