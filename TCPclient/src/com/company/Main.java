package com.company;


import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    public static int portNumber;
    public static String host;
    public static Send send;
    public static Receive receive;
    public static HeartBeat heartBeat;
    public static Socket socket;
    public static Thread sendThread;
    public static Thread receiveThread;
    public static Thread heartBeatThread;
    public static boolean isAccepted;
    public static ThreadWaiter threadWaiter = new ThreadWaiter();

    public static void main(String[] args) {


        System.out.println("Indtast IP adressen: ");
        host = scanner.nextLine();

        //Tjekker om portnummer er 4 tal uden bogstaver
        while(true) {
            System.out.println("Indtast port nummer: ");
            String portNumberString = scanner.nextLine();
            if(isNumeric(portNumberString) && portNumberString.length() == 4){
                portNumber = Integer.parseInt(portNumberString);
                break;
            } else {
                System.out.println("Port nummer er forkert format");
                continue;
            }
        }



        try {
            isAccepted = false;
            socket = new Socket(host,portNumber);

            send = new Send(socket,threadWaiter);
            receive = new Receive(socket,threadWaiter);
            heartBeat = new HeartBeat(send);

            sendThread = new Thread(send);
            sendThread.start();

            receiveThread = new Thread(receive);
            receiveThread.start();

            heartBeatThread = new Thread(heartBeat);
            heartBeatThread.start();






        } catch (IOException e) {
            if(!socket.isClosed()){
                System.out.println("Har mistet forbindelse");
                closeConnection();
            }
        }


    }

    static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public synchronized static void closeConnection(){

        try {
            socket.close();
            send.closeOut();
            receive.closeIn();
            changeBool(false);
        } catch (IOException e) {

        }

    }

    public synchronized static void changeBool(Boolean value){
        isAccepted = value;
    }

    public synchronized static boolean getBool(){
        return isAccepted;
    }





}
