package com.company;

import java.awt.*;
import java.awt.event.InputEvent;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Receiver implements Runnable {

    private Socket socket;
    private InputStream in;
    private DataInputStream dis;

    private RemoteDataServer server;

    private String message;

    public Receiver(Socket socket, RemoteDataServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("Receiving...");
        while (!socket.isClosed()) {
            try {
                in = socket.getInputStream();
                dis = new DataInputStream(in);

                message = dis.readUTF();

                System.out.println("Mess " + message);

                if (message.equals("Connectivity")){
                    displayMessage("Trying to Connect");
//                } else if(message.equals("Connected")) {
//                    displayMessage(message);
                } else {
                    displayMessage("Connected to Controller");
                    server.getBot().handleMessage(message);
                }

            } catch (IOException e) {
                System.out.println("Connection closed by peer");
                System.exit(-1);
            }
        }
    }

    private void displayMessage(String msg){
        server.setListenerMessage(msg);
    }
}
