package AgentPac;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AgentBack {
    private Agent gui;
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;

    public void setGui(Agent gui) {
        this.gui = gui;
    }

    //Todo
    public void connection (String nb) {
        try {
            s = new Socket("127.0.0.1", 7777);
            out = new DataOutputStream(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());

            out.writeUTF("Agent start by " + nb);
            gui.appendMsg("start by " + nb);

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void temp (String txPool){

        gui.appendMsg(txPool);


    }





}
