package com.company;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Listener implements Runnable {

    private Server server;
    private Socket socket;
    private DataInputStream dis;

    private String message;

    public Listener(Server server){
        this.server = server;
        this.socket = server.client;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());

            while (!socket.isClosed()) {
                System.out.println("Receiving...");

                message = dis.readUTF();

                System.out.println("Mess " + message);

                if (message != null) {
                    server.bot.handleMessage(message);
                }
            }
        } catch (IOException e) {
            server.setListenerMessage("Communication is broken");
            server.setConnectButtonEnabled(true);
            server.sThread.interrupt();
            server.stopServer();
            Thread.currentThread().interrupt();
        }
    }
}
