package com.jr.app.threadDemoTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.jr.app.config.MonitorableThreadPoolExecutor;

@Component
@EnableScheduling
public class ThreadTest {
    @Resource
    private MonitorableThreadPoolExecutor monitorableThreadPoolExecutor;

    public void mythreadTest() throws Exception, SecurityException {
        System.out.println("===============猜你猜 ===================");
        final AtomicInteger idx = new AtomicInteger(0);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                StopWatch watch = new StopWatch();
                watch.start();
                List<Future<String>> fs = new ArrayList<Future<String>>(100);
                for (int i = 0; i < 100; i++) {
                    Future<String> future = monitorableThreadPoolExecutor.submit(new RealData("name" + i));
                    fs.add(future);
                }
                try {
                    while (true) {
                        int count = monitorableThreadPoolExecutor.getActiveCount();
                        if (count > 0) {
                            Thread.sleep(1000l);
                        } else {
                            break;
                        }
                    }
                    watch.stop();
                    long time = watch.getTime();
                    System.out.println("耗时" + time);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        System.out.print("end");
        thread.start();
    }
}
