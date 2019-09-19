package Agent;

import javax.swing.*;
import java.awt.*;

public class AGui extends JFrame {
    private static final long serialVersionUID = 1L;
    private TextArea jta = new TextArea(40, 25);
    private AConnection agent = new AConnection();

    public AGui() {
        add(jta, BorderLayout.CENTER);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(800, 100, 400, 400);
        setTitle("Agent");

        agent.setAGui(this);
        agent.connect();
    }

}




