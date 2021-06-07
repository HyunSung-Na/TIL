package com.baekjun.demo.디자인패턴.singleton;

public class Aclazz {

    private SocketClient socketClient;

    public Aclazz() {
        this.socketClient = SocketClient.getInstance();
    }

    public SocketClient getSocketClient() {
        return this.socketClient;
    }
}
