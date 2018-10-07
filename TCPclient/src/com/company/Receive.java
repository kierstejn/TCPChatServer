package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;

public class Receive implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private CountDownLatch latch;
    private ThreadWaiter threadWaiter;


    public Receive(Socket socket, ThreadWaiter threadWaiter) {
        this.socket = socket;
        this.threadWaiter = threadWaiter;

    }


    @Override
    public void run() {
        try {


            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inputMessage;

            while ((inputMessage = in.readLine()) != null) {

                switch (inputMessage.substring(0,4)){
                    case "J_ER":
                        String[] failStringArray;
                        failStringArray = inputMessage.split( ":");
                        System.out.println("Fejlbesked type " + failStringArray[0].split(" ")[1] + ": " + failStringArray[1]);
                        threadWaiter.signal();
                        break;
                    case "J_OK":
                        Main.changeBool(true);
                        threadWaiter.signal();
                        System.out.println("Velkommen til chatten");
                        System.out.println("For at forlade chatten, tast: 'exit'");
                        System.out.println("For at få liste over aktive chatbrugere, tast: 'list'");
                        break;
                    case "DATA":
                        String[] dataFromUser = inputMessage.split(":");
                        if(dataFromUser[1].trim() != null) {
                            System.out.println(dataFromUser[0].split(" ")[1] + ": " + dataFromUser[1].trim());
                        }
                        break;
                    case "LIST":
                        String[] listWithUsers = inputMessage.split(" ")[1].split(":");
                        System.out.println("Brugere på chatten:");
                        for(int i = 0; i < listWithUsers.length;i++){
                            System.out.println(listWithUsers[i]);
                        }
                        break;
                    }
            }

        } catch (IOException e) {
            if(!socket.isClosed()) {
                System.out.println("Har mistet forbindelsen");
                Main.closeConnection();
            }

        }


    }

    public synchronized void closeIn(){
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

