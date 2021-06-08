package com.baekjun.demo.디자인패턴.adaptor;

public class AirConditioner implements Electronic220V{

    @Override
    public void connect() {
        System.out.println("에어컨 220v on");
    }
}
