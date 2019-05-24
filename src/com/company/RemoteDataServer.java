package com.company;

import java.io.IOException;
import java.net.*;

public class RemoteDataServer implements Runnable{

    private int PORT;

    private ServerSocket serverSocket;
    private Socket socket;

    private AutoBot bot;
    private ImageSender sender;
    private Receiver receiver;

    private ServerListener window;


    public RemoteDataServer() { bot = new AutoBot(); }

    public AutoBot getBot() { return bot; }

    public void setServerListener(ServerListener listener){ window = listener; }

    public void setPort(int port){ PORT = port; }

    public void setListenerMessage(String msg){
        System.out.println("!!!!!" + msg);
        if(window != null){
            window.setMessage(msg);
        }
    }

    public void stopServer(){
        try{
            if(serverSocket != null) {
                serverSocket.close();
                System.out.println("Server closed");
                if (socket != null) {
                    System.out.println("Client closed");
                    socket.close();
                }
            }
        }
        catch(IOException e){}
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(PORT);
            setListenerMessage("Waiting for connection on port " + PORT + "...");

        } catch(BindException e){
            setListenerMessage("Port " + PORT + " is already in use. Use a different Port");
        } catch (IOException e) {
            setListenerMessage("Unable to connect");
        } finally {
            setConnectButtonEnabled(true);
        }

        try {
            socket = serverSocket.accept();
            setListenerMessage("Client connected");
        } catch (IOException e) {
            System.err.println("Accept failed.");
        }

        try {
            // начинаем диалог с подключенным клиентом в цикле, пока сокет не закрыт
            while(!socket.isClosed()) {
                if(sender == null) {
                    sender = new ImageSender(socket);
                    sender.setImage(bot.getScreenCap());
                    Thread send_image_thread = new Thread(sender);
                    send_image_thread.start();
                }
                sender.setImage(bot.getScreenCap());

                if(receiver == null){
                    receiver = new Receiver(socket, this);
                    Thread receiver_thread = new Thread(receiver);
                    receiver_thread.start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setConnectButtonEnabled(boolean enable){
        if(window != null){
            window.setConnectButtonEnabled(enable);
        }
    }
}
