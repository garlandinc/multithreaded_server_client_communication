package server;

import server.control.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by lemon on 11/28/2016.
 */
public class Main extends JFrame {
    private JList<String> list;
    private JButton start,stop,send;
    public JTextField field;
    private JPanel buttonPanel;
    private JLabel statusBar;
    private Server server;
    private List<String> dataList;

    public Main() {
        super("Server...");
        setVisible(true);
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dataList = new ArrayList<>();


        list = new JList<>(new Vector<>(dataList));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(list), BorderLayout.CENTER);
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        add(buttonPanel, BorderLayout.NORTH);
        start = new JButton("START");
        stop = new JButton("STOP");
        send = new JButton("SEND");
        field = new JTextField(35);
        buttonPanel.add(start);
        buttonPanel.add(stop);
        buttonPanel.add(field);
        buttonPanel.add(send);
        send.setEnabled(false);
        stop.setEnabled(false);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send.setEnabled(true);
                stop.setEnabled(true);
                start.setEnabled(false);
                server=new Server(Main.this);
                server.start();
            }
        });

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                send.setEnabled(false);
                stop.setEnabled(false);
                start.setEnabled(true);
                if(server!=null){
                    server.stopAll();
                    server=null;
                }

            }
        });
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        statusBar = new JLabel("STATUS:");
        add(statusBar, BorderLayout.SOUTH);
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
