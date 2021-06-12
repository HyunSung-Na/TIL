package com.baekjun.demo.디자인패턴.aop;

import com.baekjun.demo.디자인패턴.proxy.IBrowser;

import java.util.concurrent.atomic.AtomicLong;

public class Aop {

    public static void main(String[] args) {

        AtomicLong start = new AtomicLong();
        AtomicLong end = new AtomicLong();

        IBrowser iBrowser = new AopBrowser("www.naver.com",
                () -> {
                    System.out.println("before");
                    start.set(System.currentTimeMillis());
                },
                () -> {
                    long now = System.currentTimeMillis();
                    end.set(now - start.get());
                });
        iBrowser.show();
        System.out.println("first loading time : " + end.get());

        iBrowser.show();
        System.out.println("second loading time : " + end.get());
    }
}
