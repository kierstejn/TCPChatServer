package com.company;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

public class Main {
    public static ServerSocket serverSocket = null;
    public static Socket clientSocket = null;
    public static LinkedList<ClientThread> clientThreads = new LinkedList<>();
    public static Iterator it = clientThreads.iterator();
    public static int port = 2222;

    public static void main(String[] args) {



        try {
            serverSocket = new ServerSocket(port);


            System.out.println("Server er startet");
            while(true){

                System.out.println("Venter på clients...");
                clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket,clientThreads,serverSocket);
                Thread thread = new Thread(clientThread);
                thread.start();
                clientThreads.add(clientThread);
                System.out.println("Client tilføjet");
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static synchronized Iterator clientThreadIterator(){
        Iterator it = clientThreads.iterator();
        return it;
    }
}
