package AgentPac;

import javax.swing.*;
import java.awt.*;

public class Agent extends JFrame {
    private static final long serialVersionUID = 1L;
    private TextArea jta = new TextArea(40,25);
    private AgentBack agent = new AgentBack();

    public void agentOn(String nodeNumber){
        add(jta, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(800, 100, 400, 400);
        setTitle("Agent");

        agent.setGui(this);
        agent.connection(nodeNumber);
    }

    public void appendMsg(String tx){
        jta.append(tx+"\n");
    }


}
