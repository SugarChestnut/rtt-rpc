package cn.rentaotao.netty.rpc.registry;

import cn.rentaotao.netty.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rtt
 * @date 2023/2/6 11:14
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    private final ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>(4);

    public RegistryHandler() {
        scannerClass("cn.rentaotao.netty.rpc");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof InvokerProtocol) {
            InvokerProtocol r = (InvokerProtocol) msg;
            System.out.println(r);
            String className = r.getClassName();
            if (registryMap.containsKey(className)) {
                final Object o = registryMap.get(className);
                final Method method = o.getClass().getMethod(r.getMethodName(), r.getParams());
                Object result = method.invoke(o, r.getValues());
                ctx.write(result);
                ctx.flush();
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        if (url == null) {
            return;
        }
        File f = new File(url.getFile());
        File[] files = f.listFiles();
        if (files == null) {
            return;
        }
        for (File sf : files) {
            if (sf.isDirectory()) {
                scannerClass(packageName + "." + sf.getName());
            } else {
                doRegistry(packageName + "." + sf.getName().replace(".class", ""));
            }
        }
    }

    private void doRegistry(String classPath) {
        try {
            Class<?> aClass = Class.forName(classPath);
            RpcService anno = aClass.getAnnotation(RpcService.class);
            if (anno != null) {
                Object o = aClass.getDeclaredConstructor().newInstance();
                for (Class<?> i : aClass.getInterfaces()) {
                    System.out.println("注册接口：" + i.getName());
                    System.out.println("实现类：" + o.getClass().getName());
                    registryMap.put(i.getName(), o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
