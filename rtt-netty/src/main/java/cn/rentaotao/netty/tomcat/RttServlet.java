package cn.rentaotao.netty.tomcat;

/**
 * @author rtt
 * @date 2023/2/1 14:23
 */
public class RttServlet extends AbstractServlet{


    @Override
    public void doGet(RttHttpRequest request, RttHttpResponse response) throws Exception {
        response.write("Rtt servlet");
    }

    @Override
    public void doPost(RttHttpRequest request, RttHttpResponse response) throws Exception {
        response.write("Rtt servlet");
    }
}
