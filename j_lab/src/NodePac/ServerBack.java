package NodePac;

import AgentPac.InitAgent;
import AgentPac.NodeTxPool;
import KeyPac.KeyGenerator;
import KeyPac.SigGenerator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServerBack  {
    private ServerSocket ss;
    private Socket s;
    private Server gui;
    private String tx;
    private int nodeNumber = 0;
    private Map<String, DataOutputStream> nodeMap = new HashMap<>();

    public void setGui(Server gui){
        this.gui = gui;
    }

    public void setting(){
        try{
            Collections.synchronizedMap(nodeMap);
            ss = new ServerSocket(7777);

            while (true){
                nodeNumber += 1;
                s = ss.accept();
                Receiver receiver = new Receiver(s, nodeNumber);
                receiver.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void addNode(String nodeNumber, DataOutputStream out) throws IOException{
        gui.appendMsg(nodeNumber);
        nodeMap.put(nodeNumber, out);
        sendTX(nodeNumber);
    }

    public void removeNode(String nodeNumber){
        nodeMap.remove(nodeNumber);
    }

    public void sendTX(String msg){
        Iterator<String> iterator = nodeMap.keySet().iterator();
        String key = "";

        while(iterator.hasNext()){
            key = iterator.next();
            try{
                nodeMap.get(key).writeUTF(msg);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    class Receiver extends Thread{
        private DataInputStream in;
        private DataOutputStream out;
        private String nodeNumber;

        public Receiver(Socket s, int IntNodeNumber){

            try{
                out = new DataOutputStream(s.getOutputStream());
                in = new DataInputStream(s.getInputStream());

                nodeNumber = Integer.toString(IntNodeNumber);
                addNode(nodeNumber, out);

            } catch (IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            double index = 0.0d;

            try{
                NodeTxPool txPool = new NodeTxPool();
                while (in != null){
                    tx = in.readUTF();
                    String vmMsg = vMessage(tx);
                    String pool = txPool.hashPool(vmMsg, index, nodeNumber);
                    sendTX(pool);
                    gui.appendMsg(pool);
                    index += 0.1d;

                }
            } catch (Exception e){
                removeNode(nodeNumber);
            }
        }

    }

    public HashMap<String, String> hashMapChange (String str){
        str = str.substring(1, str.length()-1);

        String[] keyValueParis = str.split(",/");
        HashMap<String, String> map = new HashMap<>();

        for (String pair : keyValueParis){
            String[] entry = pair.split("==/");
            map.put(entry[0].trim(), entry[1].trim());
        }
        return map;
    }

    public String vMessage (String tx) throws Exception{
        String msg;
        String sPubKey;
        String sig;
        PublicKey pubKey;
        boolean mVerify;
        SigGenerator verify = new SigGenerator();
        KeyGenerator key = new KeyGenerator();

        HashMap<String, String> dataMap;
        dataMap = hashMapChange(tx);

        msg = dataMap.get("msg");
        sPubKey = dataMap.get("pubKey");
        sig = dataMap.get("sig");
        pubKey = key.stringToPublicKey(sPubKey);

        mVerify = verify.verify(msg, sig, pubKey);

        if (mVerify){
            return tx;
        }
        String notVerified = "not Verified";

        return notVerified;
    }

    public static void main(String[]args){
        ServerBack serverBack = new ServerBack();
        serverBack.setting();
    }



}
