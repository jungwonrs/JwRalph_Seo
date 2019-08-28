package Controller;

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
                    continue;
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
                    in.readUTF();
                    String tx = stn.messageClassification(in.readUTF(), index);
                    broadCasting(tx);

                    if (tx.contains("Agent start by")){
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


    //TODO Agent는 빼야됨...Agent한테 까지 message가 전달됨.
    public void broadCasting(String msg){
        Iterator<String> iterator = socketMap.keySet().iterator();
        String key;

        while(iterator.hasNext()){
            key = iterator.next();
            try{
                socketMap.get(key).writeUTF(msg);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }



}
