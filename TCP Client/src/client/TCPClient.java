package client;

import socket.Main;
import utility.Constants;
import utility.Data;
import utility.Listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by lemon on 11/28/2016.
 */
public class TCPClient extends Thread{

    private final Main main;
    private Listener listener;
    private String LOG_IN_NAME;
    private boolean isRunning;
    private PrintWriter outputStream;
    private BufferedReader inputStream;
    private Socket socket;


    public TCPClient(Main main, Listener listener, String LOG_IN_NAME) {
        this.main = main;
        this.listener = listener;
        this.LOG_IN_NAME = LOG_IN_NAME;
    }

    public void sendMessage(String message) {
        if(message!=null){
            outputStream.println(""+Constants.SEND+Constants.SEND_ALL+Constants.DIVIDER_FIRST+LOG_IN_NAME+Constants.DIVIDER_SECOND+message);
            outputStream.flush();
        }
    }

    public void stopConnection() {
        isRunning=false;

        try {
            if(outputStream!=null){
                outputStream.flush();
                outputStream.close();
            }
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        listener=null;
        LOG_IN_NAME=null;
        socket=null;
        inputStream=null;
        outputStream=null;
    }

    @Override
    public void run() {
        super.run();
        startSocket();
    }

    private void startSocket() {
        isRunning=true;

        try {
            main.setStatus("Try to Connect...");
            socket=new Socket(InetAddress.getLocalHost(), Constants.TCP_PORT);
            outputStream=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            outputStream.flush();
            inputStream=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            main.setStatus("Connected...");
            sendMessage(""+Constants.LOG_IN_RELATED+Constants.LOG_IN+LOG_IN_NAME);

            while (isRunning){
                String message=null;

                try {
                    message=inputStream.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(message!=null&&message.charAt(0)-'0'==Constants.LOG_IN_RELATED){
                    checkMessage(message);
                }
                else if(listener!=null&&message!=null&&message.charAt(0)-'0'==Constants.SEND){
                    String from=message.substring(message.indexOf(Constants.DIVIDER_FIRST)+Constants.DIVIDER_FIRST_LENGTH,message.indexOf(Constants.DIVIDER_SECOND));
                    String messages=message.substring(message.indexOf(Constants.DIVIDER_SECOND)+Constants.DIVIDER_SECOND_LENGTH);

                    listener.onListen(new Data(from,LOG_IN_NAME,messages));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopConnection();
            main.stopped();
            main.setStatus("Disconnected... ");
        }
    }

    private void checkMessage(String message) {
        if(message.charAt(1)-'0'==Constants.LOG_IN_SUCCESS){
            main.setStatus("LOG IN SUCCESS");
        }
        else if(message.charAt(1)-'0'==Constants.LOG_OUT){
            main.setStatus("SERVER DISCONNECTED...");
            isRunning=false;
        }
    }
}
