package cn.rentaotao.netty.rpc.registry;

import java.lang.annotation.*;

/**
 * @author rtt
 * @date 2023/2/6 13:54
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcService {
}
