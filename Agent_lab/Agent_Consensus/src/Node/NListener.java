package Node;

import Agent.RandomSeed;
import Transaction.TxPool;

import java.io.DataOutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
       if (data.equals("s")){
               Timer timer = new Timer();
               TimerTask t = new TimerTask() {
                   @Override
                   public void run() {
                       try{
                           out.writeUTF(getNodeNumber()+"/=/="+stc.txGenerator());
                       } catch (Exception e){
                           e.printStackTrace();
                       }
                   }
               };
               TimerTask t2 = new TimerTask() {
                   @Override
                   public void run() {
                       try {
                           if (rs.randValue(seed).equals(getNodeNumber())) {
                               out.writeUTF(stc.agentStart(getNodeNumber()));
                           }
                       }catch (Exception e){
                           e.printStackTrace();
                       }
                   }
               };
               //transaction 생성 시간
               timer.schedule(t, 0, 2000);

               //Todo 처음에만 동작하고 이후에는 데이터값을 기다리게 만들어야됨.. 지금은 일정간격으로 계속 동작해버림림
              //agent 동작 시간
              timer.schedule(t2, 10000, 10000);
       }

       //save verified message
        if(data.contains("msg")){
            setTxList(txPool.saveTxPool(data));
        }

        if (data.contains("Agent on")){
            try {
                out.writeUTF("txPool"+"**"+getNodeNumber()+"**"+getTxList().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //show all message
        System.out.println(data);

    }




}
