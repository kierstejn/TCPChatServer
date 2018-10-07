package com.company;

import javax.swing.text.html.HTMLDocument;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;

public class ClientThread implements Runnable {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private String name = null;
    private PrintWriter out;
    private BufferedReader in;
    private LinkedList clientThreads;
    private String input;
    private String message;
    private boolean isAccepted = false;



    public ClientThread(Socket socket, LinkedList<ClientThread> clientThreads, ServerSocket serverSocket) {
        this.socket = socket;
        this.clientThreads = clientThreads;
        this.serverSocket = serverSocket;

    }

    public ClientThread() {
    }

    @Override
    public void run() {

        try {


            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while(true) {
                while (!isAccepted) {
                    input = in.readLine();

                    System.out.println(input);

                    switch (input.substring(0, 4)) {
                        case "JOIN":
                            String[] messageString = input.split(", ");
                            String name = messageString[0].split(" ")[1];
                            if (checkName(name)){


                               String[] socketInformation = messageString[1].split(":");

                              if (InetAddress.getLocalHost().toString().split("/")[1].equals(socketInformation[0]) ||
                                      InetAddress.getLoopbackAddress().toString().split("/")[1].equals(socketInformation[0]) &&
                                              Integer.parseInt(socketInformation[1]) == serverSocket.getLocalPort()){

                                    Iterator it = clientThreads.iterator();
                                    while(it.hasNext()){
                                        ClientThread clientThread = (ClientThread)it.next();
                                        if(clientThread.getName() == name && clientThread != this){
                                            send("J_ER 4:Navnet er i brug");
                                            break;
                                        }

                                    }
                                    this.name = name;
                                    isAccepted = true;
                                    send("J_OK");
                                    sendMessageToUsers(getListWithUsers(),false);

                                } else {
                                    System.out.println("Fejl i socket");

                                    send("J_ER 2:Forkert socket eller port");
                                }
                            } else {
                                System.out.println("navn er forkert format");
                                send("J_ER 1:Forkert navne format");
                            }
                            break;

                        default:
                            send("J_ER 3:Du er ikke accepteret. Send JOIN besked først");
                            break;

                    }

                }

                while (isAccepted && !socket.isClosed()) {

                   input = in.readLine();
                   //65 sekunder venter den på at der kommer en besked fra klient inden den afbryder
                   socket.setSoTimeout(65*1000);
                   System.out.println("Besked fra bruger: " + input);
                   if(input.length() >= 4) {
                       switch (input.substring(0, 4)) {
                           case "IMAV":
                               break;
                           case "EXIT":
                               System.out.println(name + " har forladt chatten");
                               sendMessageToUsers("DATA " + name + ": " + name + " har forladt samtalen", false);
                               sendMessageToUsers(getListWithUsers(),false);
                               clientThreads.remove(this);
                               in.close();
                               out.close();
                               socket.close();
                               break;
                           case "LIST":
                               send(getListWithUsers());
                               System.out.println("Sender liste med brugere");
                               break;
                           case "DATA":
                               sendMessageToUsers(input,false);
                               break;
                           default:
                               send("J_ER 4:Ukendt kommando");
                               break;

                       }
                   } else {
                       send("J_ER 4:Ukendt kommando");
                   }



                }
            }

        } catch (IOException e) {
            System.out.println("Forbindelsen er mistet");
            out.close();
            try {
                in.close();
                socket.close();
                clientThreads.remove(this);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } finally {
            System.out.println("Forbindelsen er lukket");
            if(!socket.isClosed()) {
                out.close();
                try {
                    in.close();
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(clientThreads.contains(this)){
                clientThreads.remove(this);
            }
        }



}

        public synchronized void send (String message){
            out.println(message);
        }

        public boolean checkName (String name){
            return name.matches("[-_æøåÆØÅa-zA-Z0-9 ]{1,12}");

        }

        public synchronized String getName(){
            return this.name;
        }

        public synchronized String getListWithUsers(){
            Iterator it = clientThreads.iterator();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("LIST ");
            while(it.hasNext()){
                ClientThread clientThread = (ClientThread) it.next();
                stringBuilder.append(clientThread.getName() + ":");

            }
            //Kapper det sidste : af
            stringBuilder.delete(stringBuilder.length()-1,stringBuilder.length());
            return stringBuilder.toString();
    }

        public synchronized void sendMessageToUsers(String output,Boolean sendToSelf){
            if(!clientThreads.isEmpty()) {
                Iterator it = clientThreads.iterator();
                while (it.hasNext()) {
                    ClientThread clientThread = (ClientThread) it.next();
                    if (clientThread != this || sendToSelf) {
                        clientThread.send(output);
                    }
                }
            }

        }





}
