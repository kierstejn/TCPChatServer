package com.company;

import static com.company.Main.isAccepted;

public class HeartBeat implements Runnable {

    private Send send;


    public HeartBeat(Send send) {
        this.send = send;
    }

    @Override
    public void run() {

        while(true){

            while(Main.getBool()){
                synchronized (this){
                    try {
                        wait(60*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                send.send("IMAV");
            }
        }

    }
}
