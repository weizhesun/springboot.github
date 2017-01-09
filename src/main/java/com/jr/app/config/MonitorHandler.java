package com.jr.app.config;

import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** 
 * 监控处理器, 目的是把before和after抽象出来, 以便在{@link MonitorableThreadPoolExecutor}中形成一条监控处理器链 
 *  
 */
@Component
public class MonitorHandler {
    private static Logger logger = LoggerFactory.getLogger(MonitorHandler.class);
    // 任务开始时间记录map
    Map<Runnable, Long> timeRecords = new ConcurrentHashMap<Runnable, Long>();
    private boolean usable = true;

    public boolean isUsable() {
        return usable;
    }

    /** 
     * 改监控任务是否可用 
     *  
     */
    public void setUsable(boolean usable) {
        this.usable = usable;
    }

    /** 
     * 任务执行前回调 
     *  
     * @param thread 即将执行该任务的线程 
     * @param runnable 即将执行的任务 
     */

    public void before(Thread thread, Runnable runnable) {
        System.out.println(String.format("%s: before[%s -> %s]", time(), thread, runnable));
        timeRecords.put(runnable, System.currentTimeMillis());
    }

    /** 
     * <pre> 
     * 任务执行后回调 
     * 注意: 
     *     1.当你往线程池提交的是{@link Runnable} 对象时, 参数runnable就是一个{@link Runnable}对象 
     *     2.当你往线程池提交的是{@link java.util.concurrent.Callable<?>} 对象时, 参数runnable实际上就是一个{@link java.util.concurrent.FutureTask<?>}对象 
     *       这时你可以通过把参数runnable downcast为FutureTask<?>或者Future来获取任务执行结果 
     *        
     * @param runnable 执行完后的任务 
     * @param throwable 异常信息 
     */
    public void after(Runnable runnable, Throwable throwable) {
        long end = 0;
        long start = 0;
        Object result = null;
        if (throwable == null && runnable instanceof FutureTask<?>) { // 有返回值的异步任务，不一定是Callable<?>，也有可能是Runnable  
            try {
                result = ((Future<?>) runnable).get();
                end = System.currentTimeMillis();
                start = timeRecords.remove(runnable);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // reset  
            } catch (ExecutionException e) {
                throwable = e;
            } catch (CancellationException e) {
                throwable = e;
            }
        } else {//Runnable记时
            end = System.currentTimeMillis();
            start = timeRecords.remove(runnable);
        }

        if (throwable == null) { // 任务正常结束  
            if (result != null) { // 有返回值的异步任务  
                System.out.println(String.format("%s: after[%s -> %s], costs %d millisecond, result: %s", time(),
                        Thread.currentThread(), runnable, end - start, result));
            } else {
                System.out.println(String.format("%s: after[%s -> %s], costs %d millisecond", time(),
                        Thread.currentThread(), runnable, end - start));
            }
        } else {
            System.out.println(String.format("%s: after[%s -> %s], costs %d millisecond, exception: %s", time(),
                    Thread.currentThread(), runnable, end - start, throwable));
        }
    }

    private String time() {
        return String.valueOf(System.currentTimeMillis());
    }
}