package com.example.tutorial6;

import java.util.ArrayList;
import java.util.List;

public class collectDataIMU implements Runnable {

    private volatile boolean running = true;
//    private volatile List<String[]> data = new ArrayList<String[]>();

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        int x =1;
        while (running) {
            //Your code that needs to be run multiple times
//            this.data.add(new String[]{"Time [sec]", "ACC X", "ACC Y", "ACC Z"});
            for (int i = 0; i < 20; i++) {
                x = x * 2;
                System.out.println(x);
            }
            System.out.println(x);
        }

    }
}