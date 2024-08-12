package cn.rentaotao.netty.rpc.provider;

import cn.rentaotao.netty.rpc.api.CalcService;
import cn.rentaotao.netty.rpc.registry.RpcService;

/**
 * @author rtt
 * @date 2023/2/2 10:19
 */
@RpcService
public class CalcServiceImpl implements CalcService {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public int sub(int a, int b) {
        return a - b;
    }
}
