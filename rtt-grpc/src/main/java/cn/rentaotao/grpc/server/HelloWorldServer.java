package cn.rentaotao.grpc.server;

import cn.rentaotao.grpc.proto.GreeterGrpc;
import cn.rentaotao.grpc.proto.Msg;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

/**
 * @author rtt
 * @date 2022/8/17 10:19
 */
public class HelloWorldServer {

    private final int port;

    private Server server;

    public HelloWorldServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                              .addService(new GreeterImpl())
                              .build()
                              .start();

        System.out.println("server started.");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("shutting down grpc server");
            HelloWorldServer.this.stop();
            System.err.println("server shutdown");
        }));
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(Msg request, StreamObserver<Msg> responseObserver) {
            System.out.println("request: " + request.getContent());
            Msg response = Msg.newBuilder().setId(request.getId() + 1).setContent("response test").build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HelloWorldServer server = new HelloWorldServer(8686);
        server.start();
        server.blockUntilShutdown();
    }
}
