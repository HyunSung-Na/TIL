package com.baekjun.demo.디자인패턴.strategy;

public class Encoder {

    private EncodingStrategy encodingStrategy;

    public String getMessage(String message) {
        return this.encodingStrategy.encoding(message);
    }

    public void setEncodingStrategy(EncodingStrategy encodingStrategy) {
        this.encodingStrategy = encodingStrategy;
    }
}
