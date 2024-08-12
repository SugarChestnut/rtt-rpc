package cn.rentaotao.netty.tomcat;

/**
 * @author rtt
 * @date 2023/2/1 16:25
 */
public abstract class AbstractServlet implements Servlet{

    @Override
    public void service(RttRequest request, RttResponse response) throws Exception {
        final String method = request.getMethod();
        if ("GET".equals(method)) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    protected abstract void doGet(RttRequest request, RttResponse response) throws Exception;

    protected abstract void doPost(RttRequest request, RttResponse response) throws Exception;
}
