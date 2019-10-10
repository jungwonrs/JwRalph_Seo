package Controller;

import Transaction.VerificationTx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class CListener {
    private CGui CGUi;
    private Map<String, DataOutputStream> socketMap = new HashMap<>();
    private ServerSocket ss;
    private Socket s;
    private int nodeNumber = 0;
    private int nodeAmount = 4;
    private List<String> voteList1 = new ArrayList<>();
    private List<String> voteList2 = new ArrayList<>();


    public void setCGui(CGui CGui){
        this.CGUi = CGUi;
    }

    public void setting(){
        try{
            Collections.synchronizedMap(socketMap);
            ss = new ServerSocket(7777);

            while (true){
                s = ss.accept();
                nodeNumber +=1;
                Receiver recever = new Receiver(Integer.toString(nodeNumber), s);
                recever.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    class Receiver extends Thread{
        private DataInputStream in;
        private DataOutputStream out;

        public Receiver(String nodeNumber, Socket s){
            try{
                out = new DataOutputStream(s.getOutputStream());
                in = new DataInputStream(s.getInputStream());

                addNode(nodeNumber, out);
                out.writeUTF("node_number_setting = "+nodeNumber);
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        public void run(){
            int countTrue;
            int countData;
            int bft;

            int countTrue2;
            int countData2;
            int bft2;

            try{
                while(in != null){
                    String tx = in.readUTF();
                    if(tx.contains("/=/=")) {
                        try {
                            broadCasting(tx);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(tx.contains("broadcasting")){
                        String[]dataSplit = tx.split("=");
                        sendMessage("vote1="+dataSplit[1]);
                    }


                    if(tx.contains("vote1")){
                        String[]dataSplit = tx.split("=");
                        String voteResult = dataSplit[1];
                        voteList1.add(voteResult);
                        if (voteList1.size() == nodeAmount){
                            countTrue = Collections.frequency(voteList1, "true");
                            countData = voteList1.size();
                            bft = countData - ((countData - 1) / 3);
                            if(countTrue >= bft){
                                sendMessage("vote1_done="+dataSplit[2]);
                                voteList1.clear();
                            }
                        }
                    }

                    if(tx.contains("vote2")){
                        String[]dataSplit = tx.split("=");
                        String voteResult = dataSplit[1];
                        voteList2.add(voteResult);
                        if (voteList2.size() == nodeAmount){
                            countTrue2 = Collections.frequency(voteList2, "true");
                            countData2 = voteList2.size();
                            bft2 = countData2 - ((countData2 - 1) / 3);
                            if (countTrue2 >= bft2){
                                sendMessage("vote2_done="+dataSplit[2]);
                                voteList2.clear();
                            }
                        }
                    }

                    if(tx.contains("block")){
                        try{
                            socketMap.get("1").writeUTF("block="+System.nanoTime());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void addNode(String nodeNumber, DataOutputStream out){
        socketMap.put(nodeNumber, out);
    }

    public void broadCasting(String tx){
        Iterator<String> iterator = socketMap.keySet().iterator();
        String key;

        while(iterator.hasNext()){
            key = iterator.next();
                try {
                    socketMap.get(key).writeUTF(tx);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    public void sendMessage(String tx){
        Iterator<String> iterator = socketMap.keySet().iterator();
        String key;

        while(iterator.hasNext()){
            key = iterator.next();
            if(!key.equals("1")) {
                try {
                    socketMap.get(key).writeUTF(tx);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    }
