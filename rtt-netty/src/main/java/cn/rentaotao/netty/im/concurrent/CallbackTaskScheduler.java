package cn.rentaotao.netty.im.concurrent;

import cn.rentaotao.common.utils.ThreadUtils;
import com.google.common.util.concurrent.*;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author rtt
 * @create 2021/3/31 09:00
 */
public class CallbackTaskScheduler {

    static ListeningExecutorService pool;

    static {
        ThreadPoolExecutor mixThreadPool = ThreadUtils.getMixThreadPool();
        pool = MoreExecutors.listeningDecorator(mixThreadPool);
    }

    private CallbackTaskScheduler() {
    }

    public static <R> void add(CallbackTask<R> callbackTask) {
        ListenableFuture<R> future = pool.submit(callbackTask::execute);
        Futures.addCallback(future, new FutureCallback<R>() {

            @Override
            public void onSuccess(R r) {
                callbackTask.onBack(r);
            }

            @Override
            public void onFailure(Throwable throwable) {
                callbackTask.onException(throwable);
            }
        });
    }
}
