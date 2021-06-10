package com.baekjun.demo.디자인패턴.strategy;

public class NormalStrategy implements EncodingStrategy{
    @Override
    public String encoding(String text) {
        return text;
    }
}
