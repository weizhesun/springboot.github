package com.jr.app.config;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(encoding = "utf-8", value = { "threadPools.properties" })
public class AppConfig {

    private int rows = 50000;
    @Value("${settle.threadNum}")
    private Integer EXECUTE_THREAD_NUM;

    @Value("${settle.name}")
    private String name;

    @Resource
    private MonitorHandler monitorHandler;

    @Bean
    public MonitorableThreadPoolExecutor testExecutor() {
        Long KEEP_ALIVE_TIME = 0L;
        MonitorableThreadPoolExecutor executorService = new MonitorableThreadPoolExecutor(EXECUTE_THREAD_NUM,
                EXECUTE_THREAD_NUM, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(rows),
                new ThreadPoolExecutor.CallerRunsPolicy());
        add(name, executorService);
        //        monitorHandler.setUsable(false);
        executorService.setMonitorHandler(monitorHandler);
        return executorService;
    }

    private void add(String name, ThreadPoolExecutor executorService) {
        ThreadMonitorJob.addThreadPools(name, executorService);
    }
}
