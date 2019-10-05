package Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CGui extends JFrame implements ActionListener {
    private static final long serialVersionUID =1L;
    private TextArea jta = new TextArea(40,25);
    private JTextField jtf = new JTextField(25);
    private CListener cl = new CListener();

    public CGui(){
        add(jta, BorderLayout.CENTER);
        add(jtf, BorderLayout.SOUTH);
        jtf.addActionListener(this);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setBounds(200, 100, 100, 100);
        setTitle("Controller");
        cl.setCGui(this);
        cl.setting();

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String command = jtf.getText();

        switch (command){
            case "s":
                cl.broadCasting("s");
        }

    }

}
