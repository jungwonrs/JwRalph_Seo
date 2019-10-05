package Transaction;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;


public class TxPool {
    private List<String> txList = new ArrayList<>();

    public String transaction (String tx, double index, String nodeNumber){
        String StrIndex = String.valueOf(index);
        String key = nodeNumber+"_"+StrIndex;

        Gson gson = new Gson();
        JsonObject obj = new JsonObject();
        obj.addProperty("index", key);
        obj.addProperty("data", tx);

        String txForm = gson.toJson(obj);

        return txForm;
    }


    public List saveTxPool(String data){
        txList.add(data);
        return txList;
    }





}
