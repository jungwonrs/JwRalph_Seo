package Node;

import Agent.RandomSeed;
import Transaction.TxPool;

import java.io.DataOutputStream;

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
                       if (rs.randValue(seed).equals(getNodeNumber())){
                           agentKey(getNodeNumber());
                       }
                   }
               };
               //transaction 생성 시간
               timer.schedule(t, 0, 2000);

               //agent 동작 시간간
              timer.schedule(t2, 10000, 10000);
       }

       //save verified message
        if(data.contains("msg")){
            setTxList(txPool.saveTxPool(data));
        }

        //show all message
        System.out.println(data);

    }

    public void agentKey(String nodeNumber) {
        System.out.println("hello! agent key!");
    }


}
