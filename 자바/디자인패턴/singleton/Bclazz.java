package com.baekjun.demo.디자인패턴.singleton;

public class Bclazz {

    private SocketClient socketClient;

    public Bclazz() {
        this.socketClient = SocketClient.getInstance();
    }

    public SocketClient getSocketClient() {
        return this.socketClient;
    }
}
