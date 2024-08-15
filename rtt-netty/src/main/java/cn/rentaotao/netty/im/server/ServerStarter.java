package cn.rentaotao.netty.im.server;

/**
 * @author rtt
 * @create 2021/4/1 13:47
 */
public class ServerStarter {

    public static void main(String[] args) {
        cn.rtt.io.netty.im.server.ImServer imServer = new cn.rtt.io.netty.im.server.ImServer();
        imServer.start();
    }
}
