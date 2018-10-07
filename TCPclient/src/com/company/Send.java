package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Send implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private Scanner scanner;
    private String name;
    private ThreadWaiter threadWaiter;


    public Send(Socket socket, ThreadWaiter threadWaiter) {
        this.socket = socket;
        this.threadWaiter = threadWaiter;
    }

    @Override
    public void run() {

        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);
            String outputMessage;

            while (!socket.isClosed()) {

                while (!Main.getBool()) {
                    System.out.println("Indtast navn: ");
                    name = scanner.nextLine();
                    send("JOIN " + name + ", " + socket.getInetAddress().toString().substring(1) + ":" + socket.getPort());
                    threadWaiter.await();
                }

                while (Main.getBool()){
                    outputMessage = scanner.nextLine();

                    switch(outputMessage) {
                        case "exit":
                            send("EXIT");
                            System.out.println("Farvel " + name);
                            break;
                        case "list":
                            send("LIST");
                            break;
                        default:
                            send("DATA " + name + ": " + outputMessage);
                            break;
                        }
                    }
                }



    } catch (IOException | InterruptedException e) {
            if(!socket.isClosed()) {
                System.out.println("Har mistet forbindelsen");
                Main.closeConnection();
            }

        }

    }

    public synchronized void send(String message) {
        out.println(message);
    }


    public synchronized void closeOut() {
        out.close();
    }
}
