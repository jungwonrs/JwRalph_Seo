package NodePac;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JTextField;

public class Server extends JFrame implements ActionListener{

    private static final long serialVersionUID =1L;
    private TextArea jta = new TextArea(40,25);
    private JTextField jtf = new JTextField(25);
    private ServerBack server = new ServerBack();

    public Server() {

        add(jta, BorderLayout.CENTER);
        add(jtf, BorderLayout.SOUTH);
        jtf.addActionListener(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(200, 100, 400, 600);
        setTitle("commander");
        server.setGui(this);
        server.setting();
    }

    public static void main(String[] args) throws IOException{
        new Server();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = jtf.getText();

        switch (command){
            case "s":
                server.sendTX("s");
                appendMsg("s");
                break;

            case "a":
                server.sendTX("a");
                appendMsg("a");
                break;


        }

    }

    public void appendMsg(String msg) {
        jta.append(msg+"\n");
    }
}