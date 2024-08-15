package cn.rentaotao.netty.im.concurrent;


import cn.rentaotao.common.utils.ThreadUtils;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author rtt
 * @create 2021/3/31 15:58
 */
public class FutureTaskScheduler {

    static ThreadPoolExecutor executor = null;

    static {
        executor = ThreadUtils.getMixThreadPool();
    }

    private FutureTaskScheduler() {}

    public static void add(ExecuteTask task) {
        executor.submit(task::execute);
    }
}
