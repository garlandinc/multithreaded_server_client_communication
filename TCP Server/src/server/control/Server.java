package server.control;

import server.Main;
import server.utility.CallBack;
import server.utility.ClientControl;
import server.utility.Constants;
import server.utility.Data;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lemon on 11/28/2016.
 */
public class Server extends Thread implements CallBack{
    private Main main;
    private HashMap<String,PrintWriter> connectedDevice;
    private ServerSocket serverSocket;
    private List<ClientControl> controls;

    public Server(Main main) {
        this.main = main;
        controls=new ArrayList<>();

        connectedDevice=new HashMap<>();
        try {
            serverSocket=new ServerSocket(Constants.TCP_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        super.run();

        for(;;){
            main.setStatus("Wait for connection...");
            try {

                Socket socket=serverSocket.accept();
                PrintWriter printWriter=new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                printWriter.flush();
                String LOG_IN_NAME=null;
                BufferedReader reader=new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while (true){
                    String line=null;
                    line=reader.readLine();
                    if(line!=null||line.charAt(0)-'0'==Constants.LOG_IN_RELATED||line.charAt(1)-'0'==Constants.LOG_IN){
                        LOG_IN_NAME=line.substring(2);
                        break;
                    }
                }
                ClientControl clientControl=new ClientControl(this,socket,printWriter,reader);
                clientControl.LOG_IN_NAME=LOG_IN_NAME;
                connectedDevice.put(LOG_IN_NAME,printWriter);
                clientControl.start();
                main.field.setText("Total connection: "+connectedDevice.size());
                controls.add(clientControl);
                main.setStatus("Connected A Device Named:"+LOG_IN_NAME);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void readMessage(String message) {

    }

    @Override
    public void readMessage(Data data) {
        int i=0;
        for(PrintWriter printWriter:connectedDevice.values()){

            if (printWriter!=null) {
                printWriter.println(""+Constants.SEND+Constants.DIVIDER_FIRST+data.FROM+Constants.DIVIDER_SECOND+data.MESSAGE);
                printWriter.flush();
                i++;
            }
        }
        main.setStatus("Total "+i+" Message Sent by Server...");
    }

    @Override
    public void removeDevice(String LOG_IN_NAME) {
        connectedDevice.remove(LOG_IN_NAME);
        main.field.setText("Total connection: "+connectedDevice.size());
    }

    public void stopAll() {
        for(ClientControl clientControl:controls){
            if (clientControl!=null) {
                clientControl.closeConnection();
            }
        }
        try {
            if(serverSocket!=null)
                serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        serverSocket=null;
        controls.clear();
        connectedDevice.clear();
        controls=null;
        connectedDevice=null;

    }
}
