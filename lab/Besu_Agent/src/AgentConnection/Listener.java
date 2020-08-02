package AgentConnection;

import Crypto.Key;
import Crypto.Sig;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.*;

import java.util.*;

public class Listener {
    private final NodeList nl = new NodeList();

    private final Map<Socket, DataOutputStream> socketMap = new HashMap<>();
    private final Map<String, String> firstCheckPool = new HashMap<>();

   public void setting(){
       try{
           ServerSocket ss;
           Socket s;

           ss = new ServerSocket(6706);

           while(true){
               s = ss.accept();

               Receiver receiver = new Receiver(s);
               receiver.start();
           }
       } catch (Exception e){
           e.printStackTrace();
       }
   }


   class Receiver extends Thread{
       private DataInputStream in;
       private DataOutputStream out;
       private Socket s;

       public Receiver(Socket s){
           this.s = s;
       }

       public void run(){
           try{
               out = new DataOutputStream(s.getOutputStream());
               in = new DataInputStream(s.getInputStream());
               addNode(s,out);

                    while (true) {
                        String data = in.readUTF();


                        if (data.contains("AgentOn")) {
                            keyNodeOn(data, out);
                        }

                        if (data.contains("AgentStart")) {
                            broadcasting("AgentStart");
                            clearMap();
                        }

                        if (data.contains("Check")) {

                            String firstResult = filterCheckStorage(data).toString();

                            System.out.println("FirstResult" + firstResult);
                            nl.getKeyNode().writeUTF("FirstResult" + firstResult);

                        }
                        ;

                        if (data.contains("hello fucking agent")) {
                            broadcasting("FirstBlock");
                        }

                        System.out.println(data);

                        //nl.getKeyNode().writeUTF("fuckyou---------------------------------------------------------------");*/
                    }



           }catch (Exception e) { e.printStackTrace(); }

           }


   }

   private HashMap<String, String> filterCheckStorage(String data) {
       String[] strPar = data.split(":");
       String parentBlockNumber = strPar[0];
       String txPool = strPar[1];
       String from = String.valueOf(strPar[2]);

       List<String> compareValue = new ArrayList<>();
       HashMap<String, String>firstCheckResult = new HashMap<>();

       synchronized (firstCheckPool) {
           firstCheckPool.put(from, txPool);
       }

       for (Map.Entry<String, String> entry : firstCheckPool.entrySet()) {
           compareValue.add(String.valueOf(entry.getValue().hashCode()));
       }

       if(firstStepCheck(compareValue)){
           firstCheckResult.put(parentBlockNumber, txPool);
           return firstCheckResult;
       }

       if(!firstStepCheck(compareValue)){
           secondStepCheck();
       }
       return null;
   }


   private void secondStepCheck(){
       System.out.println("this is second step ");
   }




      private boolean firstStepCheck(List<String> compareValue){
       for (String s : compareValue){
           if(s.equals(compareValue.get(0))){
               return true;
           }
       }
        return false;
   }

   private void clearMap(){
       synchronized (socketMap){
           socketMap.clear();
       }
   }


    private void addNode(Socket s, DataOutputStream out){
        synchronized (socketMap){
            socketMap.put(s, out);
        }
    }

    private void broadcasting(String msg){
        try {
            synchronized (socketMap){
                System.out.println(socketMap.size());

                Iterator<Socket> iterator = socketMap.keySet().iterator();
                Socket key;

                while (iterator.hasNext()) {
                    key = iterator.next();
                    socketMap.get(key).writeUTF(msg);
                    socketMap.get(key).flush();
                }
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void keyNodeOn(String data, DataOutputStream out) throws Exception {

            Key key = new Key();
            KeyPair getKey = key.genKey();
            PrivateKey privateKey = getKey.getPrivate();
            PublicKey publicKey = getKey.getPublic();

            String strPublicKey = key.pubKeyToString(publicKey);

            try {
                boolean keyNodeValidation = keyNodeCheck(data);

                if (keyNodeValidation) {

                    nl.setKeyNode(out);

                    Sig sig = new Sig();

                    String message = "AgentOn";

                    String signature = sig.genSig(privateKey, message);

                    String agentOnMessage = message + ":" + strPublicKey + ":" + signature+":"+"AgentOn";

                    nl.getKeyNode().writeUTF(agentOnMessage);
                    //System.out.println(nl.getKeyNode());
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    private boolean keyNodeCheck (String data) throws Exception {

        String[] strPar = data.split(":");

        String strPubKey = strPar[0];
        String previousBlock = strPar[1];
        int size = Integer.parseInt(strPar[2]);
        nl.setNodeCount(size);

        int blockchainRandomResult = Integer.parseInt(strPar[3]);

        Random rand = new Random();
        int seed = previousBlock.hashCode();
        rand.setSeed(seed);
        int randomResult = rand.nextInt(size);

        String message = previousBlock+":"+strPar[2]+":"+strPar[3];
        String signature = strPar[4];

        Key key = new Key();
        PublicKey pubKey = key.stringToPublicKey(strPubKey);

        Sig sv = new Sig();
        boolean sigVerify = sv.verify(message, signature, pubKey);

        if(sigVerify){
            if (blockchainRandomResult == randomResult){
                return true;
            }else{
                System.out.println("random seed algorithm result is different");
                return false;
            }
        }
        else {
            System.out.println("signature is wrong");
            return false;
        }
    }




}
