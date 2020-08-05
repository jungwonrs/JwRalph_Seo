package AgentConnection;

import Crypto.Key;
import Crypto.Sig;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;

import java.util.*;

public class Listener {
    private final NodeList nl = new NodeList();

    private final Map<Socket, DataOutputStream> socketMap = new HashMap<>();
    private final Map<String, String> firstCheckPool = new HashMap<>();
    private final List<String> txPoolList = new ArrayList<>();
    private final Set<String> commonTransaction = new HashSet<>();
    private final Set<String> troubleTransaction = new HashSet<>();

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String strPublicKey;

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
               Key key = new Key();

               KeyPair getKey = key.genKey();
               privateKey = getKey.getPrivate();
               publicKey = getKey.getPublic();
               strPublicKey = key.pubKeyToString(publicKey);


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
                          }

                          if (data.contains("Check")) {
                              String firstResult = CheckStorage(data).toString();

                              String firstResultSig = createSignature(privateKey,firstResult);

                              String firstResultMessage = strPublicKey+":"+firstResult+":"+firstResultSig+":"+"FirstResult";

                              nl.getKeyNode().writeUTF(firstResultMessage);
                          }

                          if (data.contains("FirstResultTrue")) {
                              broadcasting("FirstResultTrue");
                              clearMap();
                          }

                          if (data.contains("troubleTransactionTrue")){

                              String secondResult =  sortTxPool(commonTransaction, troubleTransaction).toString();

                              String secondResultSig = createSignature(privateKey, secondResult);

                              String secondResultMessage = strPublicKey+":"+secondResult+":"+secondResultSig+":"+"SecondResult";

                              nl.getKeyNode().writeUTF(secondResultMessage);
                          }

                          if (data.contains("troubleTransactionFalse")){
                              String secondResult =  commonTransaction.toString();

                              String secondResultSig = createSignature(privateKey, secondResult);

                              String secondResultMessage = strPublicKey+":"+secondResult+":"+secondResultSig+":"+"SecondResult";

                              nl.getKeyNode().writeUTF(secondResultMessage);                          }

                          if (data.contains("SecondResultTrue")) {
                              broadcasting("SecondResultTrue");
                              clearMap();
                          }
                          System.out.println(data);
                      }

           }
           catch (Exception e) { e.printStackTrace(); }
           }

   }


   private HashMap<String, String> CheckStorage(String data) {
       boolean sigCheck = signatureCheck(data);
       String[] strParMessage = data.split(":");
       String rawMessage = strParMessage[1];

        if(sigCheck){
            String[] strParRawMessage = rawMessage.split("@");
            String parentBlockNumber = strParRawMessage[0];
            String txPool = strParRawMessage[1];
            System.out.println("txPool"+txPool);
            String from = String.valueOf(strParRawMessage[2]);

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
                secondStepCheck(rawMessage);
            }
        }

       return null;
   }


   private void secondStepCheck(String data){
       String[] strParRawMessage = data.split("@");
       String txPool = strParRawMessage[1];
       txPool = txPool.substring(1, txPool.length()-1);
       String[] value = txPool.split(", ");


       Set<String> set = new HashSet<>();
       synchronized (txPoolList) {
           for(String str: value){
               txPoolList.add(str);
           }

           for(String str: txPoolList){
               if(!set.add(str)){
                   commonTransaction.add(str);
               }
           }

           for(String str: txPoolList){
               if(!txPoolList.contains(str)){
                   troubleTransaction.add(str);
               }
           }
       }

       String troubleTx = troubleTransaction.toString();
       String message = troubleTx.substring(1, troubleTx.length()-1);


       broadcasting(message+":"+"TroubleTransaction");

   }

   private <T> Set <T> sortTxPool(Set<T> commonTransaction, Set<T> troubleTransaction){
        return new HashSet<>(){{
            addAll(commonTransaction);
            addAll(troubleTransaction);
        }};
   }

   private boolean firstStepCheck(List<String> compareValue){
       System.out.println(compareValue);
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

    private boolean signatureCheck(String data){
       String[] strPar = data.split(":");

       String strPubKey = strPar[0];
       String message = strPar[1];
       String sig = strPar[2];

       try{
           Key key = new Key();
           PublicKey pubKey = key.stringToPublicKey(strPubKey);

           Sig sv = new Sig();
           boolean sigVerify = sv.verify(message, sig, pubKey);

           if (sigVerify){
               return true;
           }

       } catch (Exception e){
           e.printStackTrace();
           return false;
       }

       return false;
    }

    private String createSignature(PrivateKey pubKey, String message){
       try{
           Sig signature = new Sig();
           String sig = signature.genSig(pubKey, message);
           return sig;
       } catch (Exception e){
           e.printStackTrace();
       }

       return "Cannot Create Signature";
    }


}
