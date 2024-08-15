package cn.rentaotao.netty.protocol.http;

/**
 * @author rtt
 * @date 2023/2/1 14:26
 */
public interface Servlet {

    void service(RttHttpRequest request, RttHttpResponse response) throws Exception;

}
