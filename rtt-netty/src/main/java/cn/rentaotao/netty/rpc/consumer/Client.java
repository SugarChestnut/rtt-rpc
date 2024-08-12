package cn.rentaotao.netty.rpc.consumer;

import cn.rentaotao.netty.rpc.api.CalcService;
import cn.rentaotao.netty.rpc.consumer.proxy.RpcProxy;

/**
 * @author rtt
 * @date 2023/2/6 17:12
 */
public class Client {

    public static void main(String[] args) {
        CalcService calcService = RpcProxy.create(CalcService.class);
        System.out.println(calcService.add(1, 1));
    }
}
