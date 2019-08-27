package NodePac;

import AgentPac.Agent;
import AgentPac.NodeTxPool;
import AgentPac.RandomSeed;
import KeyPac.KeyGenerator;
import KeyPac.SigGenerator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.*;

public class ServerBack  {

    private ServerSocket ss;
    private Socket s;
    private Server gui;
    private String tx;
    private int nodeNumber = 0;
    private Map<String, DataOutputStream> nodeMap = new HashMap<>();
    private RandomSeed rs;

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

        if (msg.equals("a")){
            try {
                //Todo random;
                String nodeNumber = rs.randValue("a");
                nodeMap.get(nodeNumber).writeUTF("key");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

                    if (tx.contains("nodeNumber")){
                        agentOn(tx);
                        sendTX(tx);
                        continue;
                    }

                    if (tx.contains("Agent start")){
                        //Todo
                        String nodeNumber = tx.split(" ")[3];
                        if (agentCheck(nodeNumber, "a")){
                            sendTX("Agent valid");
                        }
                        continue;
                    }

                    //agent socket을 지정해줘야될듯....뭔가가 안되는디..
//                    //보낸다음에 so what?
//                    if (tx.contains("NtxPool")){
//                        System.out.println(tx);
//                        nodeMap.get("2").writeUTF(tx);
//                    }


                    String vmMsg = vMessage(tx);
                    String tx = txPool.transaction(vmMsg, index, nodeNumber);
                    sendTX(tx);
                    gui.appendMsg(tx);
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

    private String vMessage (String tx) throws Exception{
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

    private String agentOn(String tx) throws  Exception{
        String nodeNumber;
        String sPubKey;
        String sig;
        PublicKey pubKey;
        boolean agentOn;
        SigGenerator verify = new SigGenerator();
        KeyGenerator key = new KeyGenerator();
        Agent agent = new Agent();

        HashMap<String, String> dataMap;
        dataMap = hashMapChange(tx);

        nodeNumber = dataMap.get("nodeNumber");
        sPubKey = dataMap.get("pubKey");
        sig = dataMap.get("sig");
        pubKey = key.stringToPublicKey(sPubKey);

        agentOn = verify.verify(nodeNumber, sig, pubKey);

        if (agentOn){
            agent.agentOn(nodeNumber);
            return tx;
        }
        String agentOff = "not Verified";
        return agentOff;
    }

    private boolean agentCheck(String nodeNumber, String seed){
        String rValue = rs.randValue(seed);
        if (nodeNumber.equals(rValue)) {
            return true;
        }
        else{
            return false;
        }
    }


    public static void main(String[]args){
        ServerBack serverBack = new ServerBack();
        serverBack.setting();
    }

}
