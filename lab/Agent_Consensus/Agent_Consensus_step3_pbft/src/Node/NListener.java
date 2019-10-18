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


    public void getMessage(String data, DataOutputStream out) {
        if (data.contains("node_number_setting")) {
            String dataSplit[] = data.split(" = ");
            String nNumber = dataSplit[1];
            setNodeNumber(nNumber);
            System.out.println(nodeNumber);
        }

        //start message
        if (data.equals("s")) {
            Timer timer = new Timer();
            if (nodeNumber.equals("1") || nodeNumber.equals("2")) {

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
                //transaction 생성 시간 1초
                timer.schedule(t, 0, 1000);
                //transaction 생성 시간 30초
                //timer.schedule(t, 0, 30000);

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

            //agent 동작 시간 10초
            timer.schedule(t2, 0, 10000);
            //agent 동작 시간 5분
            //timer.schedule(t2, 10000, 300000);
        }

        //save verified message
        if (data.contains("msg")) {
            setTxList(txPool.saveTxPool(data));
        }

        if (data.contains("Agent on")) {
            try {
                out.writeUTF("txPool" + "&&" + getNodeNumber() + "&&" + getTxList().toString());
                getTxList().clear();
                //System.out.println("txPool"+"&&"+getNodeNumber()+"&&"+getTxList().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (data.contains("block")) {
            System.out.println(data);
        }

        if (data.contains("vote_start")) {
            try {
                out.writeUTF("vote1=true=" + data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (data.contains("vote1_done")) {
            try {
                out.writeUTF("vote2=true=" + data);
            } catch (IOException e) {
                e.printStackTrace();
           }
        }


        //show all message
        //System.out.println(data);

    }




}
