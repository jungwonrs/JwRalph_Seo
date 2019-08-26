package Node;

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


    private List<String> txList = new ArrayList<>();

    public void getMessage(String data, DataOutputStream out){
       if (data.contains("node_number_setting")){
           String dataSplit[] = data.split(" = ");
           String nNumber = dataSplit[1];
           setNodeNumber(nNumber);

        }

       //start message
       if (data.equals("s")){
               Timer timer = new Timer();
               TimerTask tt = new TimerTask() {
                   @Override
                   public void run() {
                       try{
                           out.writeUTF(getNodeNumber()+"/=/="+stc.txGenerator());
                       } catch (Exception e){
                           e.printStackTrace();
                       }
                   }
               };
               timer.scheduleAtFixedRate(tt, 0, 2000);
       }

       //save verified message
        if(data.contains("msg")){
            setTxList(txPool.saveTxPool(data));
        }

        //show all message
        //System.out.println(data);
        System.out.println(getNodeNumber());

    }

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



}
