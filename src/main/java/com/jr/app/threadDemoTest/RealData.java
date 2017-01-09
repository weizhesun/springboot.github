package com.jr.app.threadDemoTest;

import java.util.concurrent.Callable;

public class RealData implements Callable<String> {
    protected String data;

    public RealData(String data) {
        this.data = data;
    }

    @Override
    public String call() throws Exception {
        //利用sleep方法来表示真是业务是非常缓慢的
        try {
            Thread.sleep(1000);
            System.out.println("RealData==================" + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return data;
    }

    private void returndemo() {
        returntest();
    }

    private String returntest() {
        return "ok return date";
    }
}