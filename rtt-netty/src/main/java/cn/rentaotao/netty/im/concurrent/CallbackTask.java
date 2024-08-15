package cn.rentaotao.netty.im.concurrent;

/**
 * @author rtt
 * @create 2021/3/31 11:08
 */
public interface CallbackTask<R> {

    R execute() throws Exception;

    void onBack(R r);

    void onException(Throwable t);
}
