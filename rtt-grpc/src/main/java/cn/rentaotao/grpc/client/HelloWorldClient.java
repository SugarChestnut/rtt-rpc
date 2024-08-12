package cn.rentaotao.grpc.client;

import cn.rentaotao.grpc.proto.GreeterGrpc;
import cn.rentaotao.grpc.proto.Msg;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @date 2022/8/17 10:18
 */
public class HelloWorldClient {

    private final ManagedChannel channel;

    private final GreeterGrpc.GreeterBlockingStub blockingStub;

    public HelloWorldClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        blockingStub = GreeterGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public String greet(String content) {
        Msg request = Msg.newBuilder().setId(1).setContent(content).build();
        Msg response = blockingStub.sayHello(request);
        return response.getContent();
    }

    public static void main(String[] args) throws InterruptedException {
        HelloWorldClient client = new HelloWorldClient("127.0.0.1", 8686);
        String response = client.greet("request test");
        System.out.println(response);
        client.shutdown();
    }
}
