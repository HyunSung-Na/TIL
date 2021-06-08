package com.baekjun.demo.디자인패턴.adaptor;

public class SocketAdaptor implements Electronic110V{

    private Electronic220V electronic220V;

    public SocketAdaptor(Electronic220V electronic220V) {
        this.electronic220V = electronic220V;
    }

    @Override
    public void powerOn() {
        electronic220V.connect();
    }
}
