package Node;

import Agent.RandomSeed;
import Transaction.TxPool;

import java.io.DataOutputStream;

import java.io.IOException;
import java.util.*;

public class NListener {
    private String nodeNumber;
    private SendToController stc = new SendToController();
    private TxPool txPool = new TxPool();
    private RandomSeed rs = new RandomSeed();
    private String seed = "a";
    private List<String> txList = new ArrayList<>();

    public void setNodeNumber(String nodeNumber) {
        this.nodeNumber = nodeNumber;
    }
    public String getNodeNumber() {
        return nodeNumber;
    }
    public List<String> getTxList() {
        return txList;
    }
    public void setTxList(List<String> txList) {
        this.txList = txList;
    }



    public void getMessage(String data, DataOutputStream out){
       if (data.contains("node_number_setting")){
           String dataSplit[] = data.split(" = ");
           String nNumber = dataSplit[1];
           setNodeNumber(nNumber);
        }

       //start message
       if (data.equals("s")) {
           Random rand = new Random();
           int txNode = rand.nextInt(5);
                System.out.println(txNode);
                Timer timer = new Timer();
                if (txNode == 2 || txNode == 5) {

               TimerTask t = new TimerTask() {
                   @Override
                   public void run() {
                       try {
                           out.writeUTF(getNodeNumber() + "/=/=" + stc.txGenerator());
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               };
                    //transaction 생성 시간
                    timer.schedule(t, 0, 5000);

                }
               TimerTask t2 = new TimerTask() {
                   @Override
                   public void run() {
                       try {
                           if (rs.randValue(seed).equals(getNodeNumber())) {
                               out.writeUTF(stc.agentStart(getNodeNumber()));
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   }
               };

               //agent 동작 시간
               timer.schedule(t2, 10000, 50000);

       }

       //save verified message
        if(data.contains("msg")){
            setTxList(txPool.saveTxPool(data));
        }

        if (data.contains("Agent on")){
            try {
                out.writeUTF("txPool"+"&&"+getNodeNumber()+"&&"+getTxList().toString());

                //System.out.println("txPool"+"&&"+getNodeNumber()+"&&"+getTxList().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //show all message
        System.out.println(data);

    }




}
