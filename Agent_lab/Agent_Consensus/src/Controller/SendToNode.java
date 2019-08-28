package Controller;

import Transaction.TxPool;
import Transaction.VerificationAgentkey;
import Transaction.VerificationTx;



public class SendToNode {
    private VerificationTx vtx = new VerificationTx();
    private TxPool txpool = new TxPool();
    private VerificationAgentkey vak = new VerificationAgentkey();

    public String messageClassification(String data, double index) {
        String tx;
        String pool;

        if (data.contains("nodeNumber") && data.contains("pubKey"))
        {
            try {
                tx = vak.vNodeNumber(data);
                return tx;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }



        String[] dataSplit = data.split("/=/=");
        try {
            tx = vtx.vMessage(dataSplit[1]);
            pool = txpool.transaction(tx, index, dataSplit[0]);
            return pool;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}





