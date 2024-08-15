package cn.rentaotao.common.utils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author rtt
 * @create 2021/3/31 09:10
 */
public class ThreadUtils {

    /**
     * CPU 核数
     */
    public static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 保持空闲的时间
     */
    private static final int KEEP_ALIVE_SECOND = 30;

    /**
     * 有界队列的长度
     */
    private static final int QUEUE_SIZE = 10000;

    /**
     * 核心线程数
     */
    private static final int CORE_SIZE = 0;

    /**
     * 最大工作线程数
     */
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT;

    /**
     * IO 密集型线程池的最大线程数
     */
    private static final int IO_POOL_SIZE = Math.max(2, CORE_SIZE * 2);

    /**
     * IO 密集型线程池的核心线程数
     */
    private static final int IO_CORE_SIZE = 0;

    /**
     * 单例懒汉式创建线程池，用于 CPU 密集型任务（最大线程数不超过CPU核数）
     */
    private static class CpuIntenseThreadPoolLazyHolder {

        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                CORE_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECOND,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomThreadFactory("cpu")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("CPU密集型任务线程池", () -> {
                // 关闭线程池
                shutdownThreadPoolGracefully(EXECUTOR);
            }));
        }
    }

    public static ThreadPoolExecutor getCpuIntenseThreadPool() {
        return CpuIntenseThreadPoolLazyHolder.EXECUTOR;
    }

    /**
     * 单例懒汉式创建线程池，用于 IO 密集型任务（最大线程数超过CPU核数）
     */
    private static class IoIntenseThreadPoolLazyHolder {
        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                IO_CORE_SIZE,
                IO_POOL_SIZE,
                KEEP_ALIVE_SECOND,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomThreadFactory("IO")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("IO密集型任务线程池", () -> {
                // 关闭线程池
                shutdownThreadPoolGracefully(EXECUTOR);
            }));
        }
    }

    public static ThreadPoolExecutor getIoIntenseThreadPool() {
        return IoIntenseThreadPoolLazyHolder.EXECUTOR;
    }

    /**
     * 混合线程池的核心线数
     */
    private static final int MIXED_CORE_SIZE = 0;

    /**
     * 混合线程池的最大线程数
     */
    private static final int MIXED_POOL_SIZE = 128;

    private static final String MIXED_THREAD_AMOUNT = "mixed.thread.amount";

    /**
     * 单例懒汉式创建混合线程池
     */
    private static class MixThreadPoolLazyHolder {
        // 从系统环境变量中获取参数
        private static final int MAX = System.getProperty(MIXED_THREAD_AMOUNT) == null ? MIXED_POOL_SIZE : Integer.parseInt(System.getProperty(MIXED_THREAD_AMOUNT));

        private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
                MIXED_CORE_SIZE,
                MAX,
                KEEP_ALIVE_SECOND,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_SIZE),
                new CustomThreadFactory("mix")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("混合型任务线程池", () -> {
                // 关闭线程池
                shutdownThreadPoolGracefully(EXECUTOR);
            }));
        }
    }

    public static ThreadPoolExecutor getMixThreadPool() {
        return MixThreadPoolLazyHolder.EXECUTOR;
    }

    /**
     * 懒汉式创建定时和顺序性线程池
     */
    private static class SeqOrScheduledThreadPoolLazyHolder {
        // 定时任务或顺序任务
        private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(
                1,
                new CustomThreadFactory("seq")
        );

        static {
            EXECUTOR.allowCoreThreadTimeOut(true);
            Runtime.getRuntime().addShutdownHook(new ShutdownHookThread("定时和顺序任务线程池", () -> {
                // 关闭线程池
                shutdownThreadPoolGracefully(EXECUTOR);
            }));
        }
    }

    public static ScheduledThreadPoolExecutor getScheduledThreadPool() {
        return SeqOrScheduledThreadPoolLazyHolder.EXECUTOR;
    }

    /**
     * 顺序执行任务
     *
     * @param r 任务
     */
    public static void seqExecute(Runnable r) {
        getScheduledThreadPool().execute(r);
    }

    /**
     * 延迟执行任务
     *
     * @param r        任务
     * @param delay    延迟
     * @param timeUnit 单位
     */
    public static void delayExecute(Runnable r, long delay, TimeUnit timeUnit) {
        getScheduledThreadPool().schedule(r, delay, timeUnit);
    }

    /**
     * 周期执行任务
     *
     * @param r            任务
     * @param initialDelay 初始延迟时间
     * @param period       周期
     * @param timeUnit     单位
     */
    public static void scheduleAtFixedRate(Runnable r, long initialDelay, long period, TimeUnit timeUnit) {
        getScheduledThreadPool().scheduleAtFixedRate(r, initialDelay, period, timeUnit);
    }

    /**
     * 调用栈中的类名
     *
     * @param level 栈的位置 1：表示当前方法执行的堆栈，2：表示上一级方法执行的堆栈
     * @return 类名
     */
    public static String stackClassName(int level) {
        return Thread.currentThread().getStackTrace()[level].getClassName();
    }

    /**
     * 调用栈中的方法名
     *
     * @param level 栈的位置 1：表示当前方法执行的堆栈，2：表示上一级方法执行的堆栈
     * @return 方法名
     */
    public static String stackMethodName(int level) {
        return Thread.currentThread().getStackTrace()[level].getMethodName();
    }


    /**
     * 关闭线程池
     *
     * @param threadPool 线程池
     */
    public static void shutdownThreadPoolGracefully(ExecutorService threadPool) {
        if (threadPool == null || threadPool.isTerminated()) {
            return;
        }
        try {
            // 拒绝新任务,已提交的任务会被执行
            threadPool.shutdown();
        } catch (Exception e) {
            return;
        }

        try {
            if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                // 立即关闭，会返回在等待的任务集合
                threadPool.shutdownNow();
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.out.println("线程池任务未正常执行结束");
                }
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }

        // 线程池未正常关闭
        if (!threadPool.isTerminated()) {
            try {
                for (int i = 0; i < 1000; i++) {
                    if (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                    threadPool.shutdownNow();
                }
            } catch (Throwable e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * 线程工厂
     */
    private static class CustomThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNum = new AtomicInteger(1);
        private final ThreadGroup threadGroup;

        private final AtomicInteger threadNum = new AtomicInteger(1);
        private final String threadTag;

        CustomThreadFactory(String threadTag) {
            SecurityManager securityManager = System.getSecurityManager();
            threadGroup = securityManager == null ? Thread.currentThread().getThreadGroup() : securityManager.getThreadGroup();
            this.threadTag = "appPool-" + poolNum.getAndIncrement() + "-" + threadTag + "-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(threadGroup, r, threadTag + threadNum.getAndIncrement(), 0);
            if (thread.isDaemon()) {
                thread.setDaemon(false);
            }
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }
            return thread;
        }
    }

    /**
     * JVM退出时的钩子方法
     */
    private static class ShutdownHookThread extends Thread {
        private volatile boolean hasShutdown = false;
        private final Runnable callback;

        public ShutdownHookThread(String name, Runnable callback) {
            super("JVM退出钩子方法（" + name + ")");
            this.callback = callback;
        }

        @Override
        public void run() {
            synchronized (this) {
                System.out.println(getName() + " starting……");
                if (!this.hasShutdown) {
                    this.hasShutdown = true;
                    long startTime = System.currentTimeMillis();
                    try {
                        this.callback.run();
                    } catch (Exception e) {
                        System.out.println(getName() + " error: " + e.getMessage());
                    }
                    long endTime = System.currentTimeMillis();
                    System.out.println("总耗时：" + (endTime - startTime));
                }
            }
        }
    }
}
