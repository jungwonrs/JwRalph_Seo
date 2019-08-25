package Transaction;

import java.util.HashMap;

public class Converter {

    public HashMap<String, String> ChangeToMap (String data){
        data = data.substring(1, data.length()-1);

        String[] keyValueParis = data.split(",/");
        HashMap<String, String> map = new HashMap<>();

        for (String pair : keyValueParis){
            String[] entry = pair.split("==/");
            map.put(entry[0].trim(), entry[1].trim());
        }
        return map;
    }

    public HashMap<String, String> txToMap (String data){
        String msg;
        String sPubKey;
        String sig;
        String time;

        HashMap<String, String> map = new HashMap<>();

        HashMap<String, String> dataMap;
        dataMap = ChangeToMap(data);

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
