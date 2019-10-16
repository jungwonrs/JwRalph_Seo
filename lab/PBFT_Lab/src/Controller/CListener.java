package Controller;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class CListener {
    private CGui CGui;
    private Map<String, DataOutputStream> socketMap = new HashMap<>();
    private ServerSocket ss;
    private Socket s;
    private int nodeNumber = 0;

    private List<String> voteList1 = new ArrayList<>();
    private List<String> voteList2 = new ArrayList<>();

    private int nodeAmount = 0;


    public void setCGui(CGui CGui){this.CGui = CGui;}

    public void setting(){
        try{
            Collections.synchronizedMap(socketMap);
            ss = new ServerSocket(8888);

            while (true){
                s = ss.accept();
                String clientIP = String.valueOf(s.getInetAddress());

                if(clientIP.equals("/163.239.200.189")){
                    Receiver receiver = new Receiver("Client", s);
                    receiver.start();
                    continue;
                }

                nodeNumber += 1;
                Receiver receiver = new Receiver(Integer.toString(nodeNumber), s);
                receiver.start();



            }
        }catch (IOException e){
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
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        public void run(){
            try{
                while(in != null){
                    String tx = in.readUTF();
                    System.out.println(tx);
                    if(tx.contains("send_to_primary")){
                        socketMap.get("1").writeUTF(tx);
                    }

                    if(tx.contains("vote_start")){
                        broadCasting(tx);
                    }

                    if(tx.contains("vote1")){
                        String[] dataSplit = tx.split("=");
                        String voteResult = dataSplit[1];
                        voteList1.add(voteResult);

                        int countTrue = Collections.frequency(voteList1, "true");
                        int bft = nodeAmount - ((nodeAmount - 1)/ 3);

                        if (countTrue >= bft){
                            broadCasting("vote1_done="+dataSplit[2]);
                            voteList1.clear();
                        }
                    }

                    if(tx.contains("vote2")){
                        String[] dataSplit = tx.split("=");
                        String voteResult = dataSplit[1];
                        voteList2.add(voteResult);

                        int countTrue = Collections.frequency(voteList2, "true");
                        int bft = nodeAmount - ((nodeAmount - 1)/ 3);

                        if (countTrue >= bft){
                            broadCasting("vote2_done="+dataSplit[2]);
                            voteList2.clear();
                        }
                    }

                    if(tx.contains("block")){
                        sendToClient(tx);
                    }

                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void addNode(String nodeNumber, DataOutputStream out){
        socketMap.put(nodeNumber, out);
    }

    public void sendToClient(String msg){
        try{
            socketMap.get("Client").writeUTF(msg);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void broadCasting(String msg){
        Iterator<String> iterator = socketMap.keySet().iterator();
        String key;

        while(iterator.hasNext()){
            key = iterator.next();
            if(!key.equals("Client")){
                try{
                    socketMap.get(key).writeUTF(msg);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
