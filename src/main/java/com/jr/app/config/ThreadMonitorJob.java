package com.jr.app.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ThreadMonitorJob {
    private Logger logger = LoggerFactory.getLogger(ThreadMonitorJob.class);

    private static Map<String, Executor> poolsMap = new ConcurrentHashMap<String, Executor>();

    public static void addThreadPools(String threadName, ThreadPoolExecutor threadPool) {
        poolsMap.put(threadName, threadPool);
    }

    @Resource
    private ThreadPoolExecutor testExecutor;

    public static void removeThreadPools(String threadName) {
        poolsMap.remove(threadName);
    }

    @Scheduled(cron = "${cron}")
    public void execute() {
        System.out.println("========线程=======");
        System.out.println(testExecutor.toString());
        for (String threadName : poolsMap.keySet()) {
            logger.info("线程池name=\"" + threadName + "\"  当前状态={" + poolsMap.get(threadName).toString() + "}");

        }
    }

}
