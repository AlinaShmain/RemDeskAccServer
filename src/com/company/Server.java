package com.company;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private int port;
    private ServerSocket serverSocket;
    public Socket client;
    public AutoBot bot;
    private ServerListener window;

    public Thread lThread;
    public Thread sThread;

    public Server(int port) {
        this.port = port;
        bot = new AutoBot();
    }

    public void setServerListener(ServerListener listener){ window = listener; }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            setListenerMessage("Waiting for connection on port " + port + "...");
            setConnectButtonEnabled(false);
        } catch(BindException e){
            setListenerMessage("Port " + port + " is already in use. Use a different Port");
            setConnectButtonEnabled(true);
        } catch (IOException e) {
            setListenerMessage("Unable to connect");
            setConnectButtonEnabled(true);
        }

        try {
            client = serverSocket.accept();
            setListenerMessage("Client connected");
        } catch (IOException e) {
            System.err.println("Accept failed.");
        }

        Listener listener = new Listener(this);
        Sender sender = new Sender(this);

        lThread = new Thread(listener);
        lThread.start();
        sThread = new Thread(sender);
        sThread.start();
    }

    public void stopServer(){
        try{
            if(serverSocket != null) {
                serverSocket.close();
                System.out.println("Server closed");
                if (client != null) {
                    client.close();
                    System.out.println("Client closed");
                }
            }
        }
        catch(IOException e){}
    }

    public void setListenerMessage(String msg){
        System.out.println("!!!!!" + msg);
        if(window != null){
            window.setMessage(msg);
        }
    }

    public void setConnectButtonEnabled(boolean enable){
        if(window != null){
            window.setConnectButtonEnabled(enable);
        }
    }
}
