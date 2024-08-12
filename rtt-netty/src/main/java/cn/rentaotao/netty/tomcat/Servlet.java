package cn.rentaotao.netty.tomcat;

/**
 * @author rtt
 * @date 2023/2/1 14:26
 */
public interface Servlet {

    void service(RttRequest request, RttResponse response) throws Exception;

}
