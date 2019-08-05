package NodePac;

import javax.swing.*;
import java.awt.*;

public class Node extends JFrame {
    private static final long serialVersionUID = 1L;
    private TextArea jta = new TextArea(40,25);
    private NodeBack node = new NodeBack();

    public Node() {

        add(jta, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(800, 100, 400, 400);
        setTitle("Node");

        node.setGui(this);
        node.connection();
    }


    public void appendMsg(String tx){
       jta.append(tx+"\n");
    }

    public static void main(String[] args){
        new Node();
    }

}
