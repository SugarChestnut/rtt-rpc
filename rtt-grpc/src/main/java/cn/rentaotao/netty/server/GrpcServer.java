package cn.rentaotao.netty.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author rtt
 * @date 2024/8/12 16:55
 */
public class GrpcServer {
    public static final ThreadPoolExecutor sdkRpcExecutor = new ThreadPoolExecutor(
            60,
            60,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(16384),
            new ThreadFactoryBuilder().daemon(true).nameFormat("nacos-grpc-executor-%d").build());

    public void start() {
        NettyServerBuilder.forPort(9090).executor()
    }

    public static void main(String[] args) {

    }
}
