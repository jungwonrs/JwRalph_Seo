package Controller;

import Transaction.TxPool;
import Transaction.VerificationAgentkey;
import Transaction.VerificationTx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CListener {
    private CGui CGui;
    private Map<String, DataOutputStream> socketMap = new HashMap<>();
    private ServerSocket ss;
    private Socket s;
    private int nodeNubmer = 0;
    private SendToNode stn = new SendToNode();


    public void setCGui(CGui CGui){
        this.CGui = CGui;
    }

    public void setting() {
        try{
            Collections.synchronizedMap(socketMap);
            ss = new ServerSocket(7777);

            while (true){
                s = ss.accept();
                String agentIp = String.valueOf(s.getInetAddress());
                if (agentIp.equals("/163.239.200.189")){
                    new Receiver("Agent",s);
                    //continue;
                }
                nodeNubmer += 1;
                Receiver receiver = new Receiver(Integer.toString(nodeNubmer), s);
                receiver.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

   class Receiver extends Thread {
        private DataInputStream in;
        private DataOutputStream out;
        private VerificationTx vtx = new VerificationTx();
        private TxPool txpool = new TxPool();
        private VerificationAgentkey vak = new VerificationAgentkey();

        public Receiver(String nodeNubmer, Socket s){
            try{
                out =  new DataOutputStream(s.getOutputStream());
                in = new DataInputStream(s.getInputStream());

                addNode(nodeNubmer, out);
                out.writeUTF("node_number_setting = "+nodeNubmer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run(){
            double index = 0.0d;
            try {
                while(in != null) {
                    String tx = in.readUTF();
                    System.out.println(tx);
                    String data;
                    String pool;

                    if (tx.contains("/=/=")) {
                        String[] dataSplit = tx.split("/=/=");
                        try {
                            data = vtx.vMessage(dataSplit[1]);
                            pool = txpool.transaction(data, index, dataSplit[0]);
                            broadCasting(pool);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                    if (tx.contains("nodeNumber") && tx.contains("pubKey")) {
                        try {
                            System.out.println("okay!");
                            data = vak.vNodeNumber(tx);
                            System.out.println(data);
                            broadCasting(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (tx.contains("Agent start by")) {
                        socketMap.get("Agent").writeUTF(tx);
                    }

                    if (tx.contains("Agent on")) {
                        broadCasting(tx);
                    }

                    if (tx.contains("txPool")) {
                        socketMap.get("Agent").writeUTF(tx);
                    }


                    index += 0.01d;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

   }

   public void addNode(String nodeNumber, DataOutputStream out){
        socketMap.put(nodeNumber, out);
   }


    public void broadCasting(String msg){
        Iterator<String> iterator = socketMap.keySet().iterator();
        String key;

        while(iterator.hasNext()){
            key = iterator.next();
            if (!key.equals("Agent")) {
                try {
                    socketMap.get(key).writeUTF(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}
