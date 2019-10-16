package Node;

import Transaction.VerificationTx;

import java.io.DataOutputStream;
import java.io.IOException;


public class NListener {
    private String nodeNumber;


    private VerificationTx vtx = new VerificationTx();
    private void setNodeNumber(String nodeNumber){this.nodeNumber = nodeNumber;}
    private String getNodeNumber() {return nodeNumber;}


    public void getMessage(String data, DataOutputStream out){
        if(data.contains("node_number_setting")){
            String dataSplit[] = data.split(" = ");
            String nNumber = dataSplit[1];
            setNodeNumber(nNumber);
            System.out.println(nodeNumber);
        }

        if(data.contains("send_to_primary")){
            String[] dataSplit = data.split("=");
            try{
                out.writeUTF("vote_start="+dataSplit[1]);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        if(data.contains("vote_start")){
            String [] dataSplit = data.split("=");
            try{
                String message = vtx.vMessage(dataSplit[1]);
                out.writeUTF("vote1=true="+message);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if(data.contains("vote1_done")){
            String [] dataSplit = data.split("=");
            try{
                String message = vtx.vMessage(dataSplit[1]);
                out.writeUTF("vote2=true="+message);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if(data.contains("vote2_done")){
            String[] dataSplit = data.split("=");
            try{
                out.writeUTF("block="+dataSplit[1]);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }




}
