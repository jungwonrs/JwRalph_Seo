package NodePac;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import AgentPac.*;
import KeyPac.*;

public class NodeBack {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private Node gui;

    public void setGui(Node gui) {
        this.gui = gui;
    }


    public void connection() {
        Timer timer = new Timer();
        String nodeNumber;
        Agent agent = new Agent();

        try{
            s = new Socket("127.0.0.1", 7777);
            out = new DataOutputStream(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());

            nodeNumber = in.readUTF();
            gui.appendMsg(nodeNumber);

            while (in != null) {
                String command = in.readUTF();
                gui.appendMsg(command);

                switch (command) {
                    case "s":
                        TimerTask tt = new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    sendTx();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        timer.scheduleAtFixedRate(tt, 0, 2000);
                        break;

                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendTx () throws Exception{
        Random random = new Random();
        String tx;
        String msg;
        String time;
        String pubKey;
        String sig;

        KeyGenerator key = new KeyGenerator();
        KeyPair getKey = key.genKey();
        PublicKey pkey = getKey.getPublic();

        SigGenerator sg = new SigGenerator();

        try {
             msg = String.valueOf(random.nextInt());
             time = String.valueOf(Instant.now().getEpochSecond());
             pubKey =key.pubKeyToString(pkey);
             sig = sg.genSig(getKey.getPrivate(), msg);

            tx = "{msg==/"+msg+",/time==/"+time+",/pubKey==/"+pubKey+",/sig==/"+sig+"}";
            out.writeUTF(tx);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[]args){
        NodeBack back = new NodeBack();
        back.connection();
    }

}