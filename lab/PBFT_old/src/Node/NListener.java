package Node;

import Transaction.VerificationTx;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class NListener {
    private String nodeNumber;
    private SendToController stc = new SendToController();

    private void setNodeNumber(String nodeNumber) {this.nodeNumber = nodeNumber;}
    private String getNodeNumber() {return nodeNumber;}

    private Long startTime;
    private VerificationTx vtx = new VerificationTx();

    public void getMessage(String data, DataOutputStream out){
        startTime = System.nanoTime();

        if (data.contains("node_number_setting")){
            String dataSplit[] = data.split(" = ");
            String nNumber = dataSplit[1];
            setNodeNumber(nNumber);
            System.out.println(nodeNumber);
        }

        if (data.equals("s")) {
            Timer timer = new Timer();
            //PBFT CLIENT
            if (nodeNumber.equals("1")){
                TimerTask t = new TimerTask() {
                    @Override
                    public void run() {
                        try{
                            System.out.println("StratTime="+startTime);
                            out.writeUTF(getNodeNumber() + "/=/=" + stc.txGenerator());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                timer.schedule(t,0,10000);
            }
        }

        if (data.contains("/=/=")){
            if(nodeNumber.equals("2")){
                String[] dataSplit = data.split("/=/=");
                try{
                    String message = vtx.vMessage(dataSplit[1]);
                    out.writeUTF("broadcasting="+message);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if(data.contains("vote1")){
            String[] dataSplit = data.split("=");
            try{
                String message = vtx.vMessage(dataSplit[1]);
                out.writeUTF("vote1=true="+message);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if(data.contains("vote1_done")){
            String[] dataSplit = data.split("=");
            try{
                String message = vtx.vMessage(dataSplit[1]);
                out.writeUTF("vote2=true="+message);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if(data.contains("vote2_done")){
            String[] dataSplit = data.split("=");
            try {
                out.writeUTF("block="+dataSplit[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(data.contains("block")){
            System.out.println(data);
        }
        //System.out.println(data);
    }
}
