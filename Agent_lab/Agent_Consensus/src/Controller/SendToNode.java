package Controller;

import Transaction.TxPool;
import Transaction.VerificationTx;


public class SendToNode {
    private VerificationTx vtx = new VerificationTx();
    private TxPool txpool = new TxPool();


    public String messageClassification(String data, double index) {
        String tx;
        String pool;

        if (data.contains("msg") && data.contains("time")) {
            String[] dataSplit = data.split("/=/=");
            try {
                tx = vtx.vMessage(dataSplit[1]);
                pool = txpool.transaction(tx, index, dataSplit[0]);
                return pool;

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

      return null;
    }

}



