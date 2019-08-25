package Node;

import Transaction.TxPool;

import java.io.DataOutputStream;

import java.util.Timer;
import java.util.TimerTask;

public class NListener {
    private String nodeNumber;
    private SendToController stc = new SendToController();
    private TxPool txpool = new TxPool();

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

       //verified message
        if(data.contains("msg")){
            //System.out.println(data);
            //txpool.timeControl(data, getNodeNumber());

        }


    }

    public void setNodeNumber(String nodeNumber) {
        this.nodeNumber = nodeNumber;
    }
    public String getNodeNumber() {
        return nodeNumber;
    }



}
