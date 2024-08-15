package cn.rentaotao.netty.im.client;

/**
 * @author rtt
 * @create 2021/4/1 09:43
 */
public class ClientStarter {

    public static void main(String[] args) {
        ClientController clientController = new ClientController();
        clientController.start();
    }
}
