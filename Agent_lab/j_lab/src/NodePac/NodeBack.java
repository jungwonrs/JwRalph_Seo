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

import AgentPac.AgentBack;
import AgentPac.NodeTxPool;
import KeyPac.*;

public class NodeBack {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private Node gui;
    private String nodeNumber;
    private NodeTxPool tx = new NodeTxPool();
    private String txPool = "";
    public void setGui(Node gui) {
        this.gui = gui;
    }


    public void connection() {
        //Timer timer = new Timer();
//        String nodeNumber;
//        NodeTxPool tx = new NodeTxPool();
//        String txPool = "";
        try{
            s = new Socket("127.0.0.1", 7777);
            out = new DataOutputStream(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());

            nodeNumber = in.readUTF();
            System.out.println(nodeNumber);
            gui.appendMsg(nodeNumber);

            while (in != null) {
                String command = in.readUTF();

                if (command.contains("msg")){
                    AgentBack ab = new AgentBack();
                    gui.appendMsg(command);
                    txPool = String.valueOf(tx.txPool(command));
                    ab.temp(txPool);
                    //System.out.println(txPoll);
                    continue;
                }

                switch (command){
                    case "s":
                        txGenerator();
                        break;
                    case "key":
                        agentStart(nodeNumber);
                        break;
                    case "Agent valid":
                        AgentBack ab = new AgentBack();
                        //ab.temp(nodeNumber);

                        //System.out.println(nodeNumber);
                        //gui.appendMsg("hello!~!@~!@ agent!!!!!");

                }

                }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void txGenerator(){
        Timer timer = new Timer();
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

    public void agentStart(String nodeNumber)  {
        KeyGenerator key = new KeyGenerator();
        String tx;
        try {

            KeyPair getKey = key.genKey();
            PublicKey pkey = getKey.getPublic();
            SigGenerator sg = new SigGenerator();
            String msg = nodeNumber;
            String pubKey =key.pubKeyToString(pkey);
            String sig = sg.genSig(getKey.getPrivate(), msg);

            tx = "{nodeNumber==/"+nodeNumber+",/pubKey==/"+pubKey+",/sig==/"+sig+"}";
            out.writeUTF(tx);


        } catch (Exception e) {
            e.printStackTrace();
        }
        }


    public static void main(String[]args){
        NodeBack back = new NodeBack();
        back.connection();
    }


}