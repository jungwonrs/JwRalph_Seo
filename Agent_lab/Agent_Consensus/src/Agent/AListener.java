package Agent;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class AListener {
    private HashMap<String, Integer> hashStorage = new HashMap<String, Integer>();
    //network node 수
    private Integer nodeAmount = 4;

    public void messageHandler(String data, DataOutputStream out) {
        if (data.contains("Agent start by")) {
            try {
                out.writeUTF("Agent on");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Agent algorithm......
        if (data.contains("txPool")){
            try{
                firstCheck(data, out);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private void firstCheck(String data, DataOutputStream out) throws IOException {
        String[] dataSplit = data.split("&&");
        String nodeNumber = dataSplit[1];
        String txData = dataSplit[2];
        String jsonIndex;
        List<String>indexList = new ArrayList<>();

        int hashData = txData.hashCode();
        hashStorage.put(nodeNumber, hashData);

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(gson.toJson(txData));
        jsonIndex=element.getAsJsonObject().get("index").getAsString();
        indexList.add(jsonIndex);

        if (hashStorage.size() == nodeAmount) {

            Map<Integer, Integer> counts = new HashMap<>();
            for (Integer c : hashStorage.values()) {
                int value = counts.get(c) == null ? 0 : counts.get(c);
                counts.put(c, value + 1);
            }

            int max = Collections.max(counts.values());

            if (max >= nodeAmount - ((nodeAmount - 1) / 3)) {
                for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
                    if (entry.getValue().equals(max)) {

                        System.out.println(indexList);
                        //out.writeUTF("block");
                        //System.out.println(entry.getKey());
                    }
                }
            } else {
                secondCheck(hashStorage);
            }
        }
    }

    private void secondCheck(HashMap<String, Integer> data){
        System.out.println("second");
    }

}
