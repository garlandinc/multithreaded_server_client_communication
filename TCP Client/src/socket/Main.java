package socket;

import client.TCPClient;
import utility.Constants;
import utility.Data;
import utility.Listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by lemon on 11/28/2016.
 */
public class Main extends JFrame{
    private JList<String> list;
    private JButton start,stop,send;
    private JTextField field;
    private JPanel buttonPanel;
    private JLabel statusBar;
    private TCPClient client;
    private List<String> dataList;

    public Main() {
        super("Client Socket...");
        setVisible(true);
        setSize(1000,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dataList=new ArrayList<>();
        /*dataList.add("Lemon");
        dataList.add("Mehedi");*/

        list=new JList<>(new Vector<>(dataList));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list),BorderLayout.CENTER);
        buttonPanel=new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        add(buttonPanel,BorderLayout.NORTH);
        start=new JButton("START");
        stop=new JButton("STOP");
        send=new JButton("SEND");
        field=new JTextField(35);
        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.add(field);
        buttonPanel.add(send);
        send.setEnabled(false);
        stop.setEnabled(false);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(field.getText().length()>=4){
                    send.setEnabled(true);
                    stop.setEnabled(true);
                    start.setEnabled(false);

                    client=new TCPClient(Main.this, new Listener() {
                        @Override
                        public void onListen(Data message) {
                            dataList.add(message.FROM+":"+message.MESSAGE);
                            list.setListData(new Vector<String>(dataList));
                        }
                    },field.getText());
                    field.setText("");

                    client.start();
                }
                else {
                    JOptionPane.showConfirmDialog(Main.this,"You should give a UserName on TextBox which have a Length min:4 ","Connection Alert",JOptionPane.DEFAULT_OPTION,JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send.setEnabled(false);
                stop.setEnabled(false);
                start.setEnabled(true);

                if(client!=null){
                    client.sendMessage(""+Constants.LOG_IN_RELATED+Constants.LOG_OUT);
                    client.stopConnection();
                    client=null;
                }
            }
        });
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(client!=null){
                    client.sendMessage(field.getText());
                    dataList.add("-->:"+field.getText());
                    list.setListData(new Vector<String>(dataList));
                    setStatus("Message was sent...");
                    field.setText("");field.setText("");
                }
            }
        });

        statusBar=new JLabel("STATUS:");
        add(statusBar,BorderLayout.SOUTH);
    }


    public static void main(String[] args){
        new Main();
    }

    public void setStatus(String status){
        statusBar.setText("STATUS:"+status);
    }

    public void stopped() {
        send.setEnabled(false);
        stop.setEnabled(false);
        start.setEnabled(true);
    }
}
