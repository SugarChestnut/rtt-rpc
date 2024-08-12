package cn.rentaotao.netty.rpc.consumer.proxy;

import cn.rentaotao.netty.rpc.protocol.InvokerProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author rtt
 * @date 2023/2/6 16:08
 */
public class RpcProxy {

    public static <T> T create(Class<T> clazz) {
        Class<?>[] interfaces = clazz.isInterface() ? new Class[]{clazz} : clazz.getInterfaces();
        @SuppressWarnings("all")
        T o = (T) Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, new MethodProxy(clazz));
        return o;
    }

    private record MethodProxy(Class<?> clazz) implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class.equals(clazz.getDeclaringClass())) {
                try {
                    return method.invoke(clazz, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return rpcInvoke(proxy, method, args);
            }

            return null;
        }

        public Object rpcInvoke(Object proxy, Method method, Object[] args) {
            InvokerProtocol protocol = new InvokerProtocol(
                    this.clazz.getName(),
                    method.getName(),
                    method.getParameterTypes(),
                    args
            );
            RpcProxyHandler rpcProxyHandler = new RpcProxyHandler();
            NioEventLoopGroup group = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(group)
                         .channel(NioSocketChannel.class)
                         .option(ChannelOption.TCP_NODELAY, true)
                         .option(ChannelOption.SO_KEEPALIVE, true)
                         .handler(new ChannelInitializer<SocketChannel>() {
                             @Override
                             protected void initChannel(SocketChannel socketChannel) throws Exception {
                                 ChannelPipeline pipeline = socketChannel.pipeline();
                                 pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                 pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
                                 pipeline.addLast("encoder", new ObjectEncoder());
                                 pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
                                 pipeline.addLast(rpcProxyHandler);
                             }
                         });
                ChannelFuture f = bootstrap.connect("127.0.0.1", 8080).sync();
                f.channel().writeAndFlush(protocol).sync();
                f.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                group.shutdownGracefully();
            }
            return rpcProxyHandler.getResponse();
        }
    }
}
