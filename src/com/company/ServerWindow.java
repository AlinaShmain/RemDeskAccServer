package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ServerWindow extends ServerListener implements ActionListener {

    public Server server;

    private Thread sThread;

    private static final int WINDOW_HEIGHT = 275;
    private static final int WINDOW_WIDTH = 350;

    private JFrame window = new JFrame("Remote Desktop Server");

    private JLabel portLabel = new JLabel("PORT: ");

    private JTextArea[] buffers = new JTextArea[4];
    private JTextField portTxt = new JTextField(5);

    private JLabel serverMessages = new JLabel("Not Connected");

    private JButton connectButton = new JButton("Connect");
    private JButton disconnectButton = new JButton("Disconnect");
    private JButton shutdownButton = new JButton("Shutdown");


    public ServerWindow(){

        window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        connectButton.addActionListener(this);
        disconnectButton.addActionListener(this);
        shutdownButton.addActionListener(this);

        Container c = window.getContentPane();
        c.setLayout(new FlowLayout());

        int x;
        for(x = 0; x < 4; x++){
            buffers[x] = new JTextArea("", 1, 30);
            buffers[x].setEditable(false);
            buffers[x].setBackground(window.getBackground());
        }

        c.add(buffers[0]);
        c.add(portLabel);
        portTxt.setText("5444");
        c.add(portTxt);

        c.add(buffers[3]);

        c.add(buffers[1]);
        c.add(connectButton);
        c.add(disconnectButton);
        c.add(buffers[2]);
        c.add(serverMessages);

        window.setLocationRelativeTo(null);
        window.setVisible(true);
        window.setResizable(false);
    }

    public void actionPerformed(ActionEvent e){
        Object src = e.getSource();

        if(src instanceof JButton){
            if((JButton)src == connectButton){
                int port = Integer.parseInt(portTxt.getText());
                runServer(port);
            } else if((JButton)src == disconnectButton){
                closeServer();
                setConnectButtonEnabled(true);
            } else if((JButton)src == shutdownButton){
                closeServer();
                System.exit(0);
            }
        }
    }

    public void setMessage(String msg){
        serverMessages.setText(msg);
    }

    public void setConnectButtonEnabled(boolean enable){
        connectButton.setEnabled(enable);
    }

    public void runServer(int port){
        if(port < 65535){
            server = new Server(port);
            server.setServerListener( this );

            sThread = new Thread(server);
            sThread.start();
            setConnectButtonEnabled(false);
        }else{
            setMessage("The port Number must be less than 65535");
            setConnectButtonEnabled(true);
        }
    }

    public void closeServer(){
        server.stopServer();
        setMessage("Disconnected");
    }

    public static void main(String[] args){
        new ServerWindow();
    }
}
