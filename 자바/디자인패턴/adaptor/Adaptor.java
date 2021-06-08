package com.baekjun.demo.디자인패턴.adaptor;

public class Adaptor {

    public static void main(String[] args) {
        HairDryer hairDryer = new HairDryer();
        connect(hairDryer);

        Cleaner cleaner = new Cleaner();
        Electronic110V adaptor = new SocketAdaptor(cleaner);
        connect(adaptor);

        AirConditioner airConditioner = new AirConditioner();
        Electronic110V airAdaptor = new SocketAdaptor(airConditioner);
        connect(airAdaptor);
    }

    public static void connect(Electronic110V electronic110V) {
        electronic110V.powerOn();
    }
}
