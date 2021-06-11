package com.baekjun.demo.디자인패턴.observer;

public class Button {

    private String name;
    private IButtonListener iButtonListener;

    public Button(String name) {
        this.name = name;
    }

    public void click(String message) {
        iButtonListener.clickEvent(message);
    }

    public void addListener(IButtonListener iButtonListener) {
        this.iButtonListener = iButtonListener;
    }
}
