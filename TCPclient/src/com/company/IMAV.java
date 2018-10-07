package com.company;

import static com.company.Main.isAccepted;

public class IMAV implements Runnable {

    public IMAV() {
    }

    @Override
    public void run() {

        while (true) {

            while (Main.getBool()) {
                System.out.println("Er accepteret");
            }
        }
    }
}
