package com.jr.app;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jr.app.threadDemoTest.ThreadTest;

@Controller
public class ThreadController {

    @Resource
    private ThreadTest threadTest;

    @RequestMapping("/h")
    @ResponseBody
    public String home() {
        try {
            threadTest.mythreadTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "hello";
    }

    @RequestMapping("/w")
    public String work() {

        return "work";
    }

}
