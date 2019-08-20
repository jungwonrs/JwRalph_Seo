package AgentPac;

import NodePac.ServerBack;

import java.util.*;

public class NodeTxPool {

public String hashPool(String tx, double index, String nodeNumber){
    String StrIndex = String.valueOf(index);
    String key = nodeNumber+"_"+StrIndex;

    HashMap<String, HashMap<String, String>> txPool = new HashMap<>();
    HashMap<String, String> map = newHashMap(tx);


    txPool.put(key, map);
    String sTxPool = txPool.toString();

    return sTxPool;
}


private static HashMap<String, String> newHashMap (String data){
    String msg;
    String sPubKey;
    String sig;
    String time;

    HashMap<String, String> map = new HashMap<>();

    ServerBack getHashMap = new ServerBack();
    HashMap<String, String> dataMap;
    dataMap = getHashMap.hashMapChange(data);

    msg = dataMap.get("msg");
    sPubKey = dataMap.get("pubKey");
    sig = dataMap.get("sig");
    time = dataMap.get("time");

    map.put("msg", msg);
    map.put("pubKey", sPubKey);
    map.put("sig", sig);
    map.put("time", time);

    return map;
}


}
