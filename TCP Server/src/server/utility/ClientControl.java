package server.utility;

import server.control.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by lemon on 12/17/2016.
 */
public class ClientControl extends Thread{
    private Server server;
    private CallBack callBack;
    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    public String LOG_IN_NAME=null;
    private boolean isRunning=false;

    public ClientControl(Server server, Socket socket, PrintWriter printWriter, BufferedReader bufferedReader) {
        this.server = server;
        this.socket = socket;
        this.printWriter = printWriter;
        this.bufferedReader = bufferedReader;
        this.callBack = (CallBack)server;
    }

    @Override
    public void run() {
        super.run();
        isRunning=true;

        try {
            while (isRunning){
                String message=null;
                try {
                    message=bufferedReader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(message!=null&&message.charAt(0)-'0'!=Constants.LOG_IN_RELATED){
                    String from=message.substring(message.indexOf(Constants.DIVIDER_FIRST)+Constants.DIVIDER_FIRST_LENGTH,message.indexOf(Constants.DIVIDER_SECOND));
                    String to=""+message.charAt(1);
                    String msg=message.substring(message.indexOf(Constants.DIVIDER_SECOND)+Constants.DIVIDER_SECOND_LENGTH);
                    Data data=new Data(from,to,msg);
                    data.TOALL=true;
                    callBack.readMessage(data);
                }
                else if(message.charAt(0)-'0'==Constants.LOG_IN_RELATED&&message.charAt(1)-'0'==Constants.LOG_OUT){
                    isRunning=false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    public void closeConnection() {
        isRunning=false;
        if(printWriter!=null)
            printWriter.flush();
        if(printWriter!=null)
            printWriter.close();
        try {
            if(bufferedReader!=null)
                bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if(socket!=null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter=null;
        bufferedReader=null;
        socket=null;

        callBack.removeDevice(LOG_IN_NAME);
    }
}
