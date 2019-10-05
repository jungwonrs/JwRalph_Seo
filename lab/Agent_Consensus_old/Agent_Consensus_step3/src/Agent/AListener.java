package Agent;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class AListener {
    private HashMap<String, Integer> hashStorage = new HashMap<String, Integer>();
    //network node 수
    private Integer nodeAmount = 24;
    private Long endTime;
    private Long startTime;
    private Long executeTime;

    private List<String> indexList = new ArrayList<>();
    private List<String> dataList = new ArrayList<>();
    private List<String> sendList = new ArrayList<>();
    private List<String> dataList2 = new ArrayList<>();
    private List<String> voteList = new ArrayList<>();

    private HashMap<String, Integer> indexMap = new HashMap<>();
    private HashMap<String, List<String>> dataMap = new HashMap<>();
    private HashMap<String, Integer> hashStorage2 = new HashMap<>();
    private HashMap<String, Integer> dataAmount = new HashMap<>();


    public void messageHandler(String data, DataOutputStream out) {
        startTime = System.nanoTime();
        System.out.println("start time1=" +startTime);
        if (data.contains("Agent start by")) {
            try {
                out.writeUTF("Agent on");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Agent algorithm......
        if (data.contains("txPool")) {
            try {
                firstCheck(data, out);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (data.contains("voting_result")){
            try{
                votingCheck(data, out);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    private void firstCheck(String data, DataOutputStream out) throws IOException {
        String[] dataSplit = data.split("&&");
        String nodeNumber = dataSplit[1];
        String txData = dataSplit[2];
        String index;
        List<String> indexList = new ArrayList<>();
        HashMap<Integer, Integer> counts = new HashMap<>();

        int hashData = txData.hashCode();
        hashStorage.put(nodeNumber, hashData);


        if (hashStorage.size() == nodeAmount) {
            String stringTxData = txData.replaceAll("[\\[\\]]", "");
            String[] splitTxData = stringTxData.split(", ");
            for (int i = 0; i < splitTxData.length; i++) {
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(splitTxData[i]);
                index = element.getAsJsonObject().get("index").getAsString();
                indexList.add(index);
            }

            for (Integer c : hashStorage.values()) {
                int value = counts.get(c) == null ? 0 : counts.get(c);
                counts.put(c, value + 1);
            }

            //int max = Collections.max(counts.values());
            int max = 0;
            int bft = nodeAmount - ((nodeAmount - 1) / 3);

            if (max >= bft) {
                Set set = counts.keySet();
                Iterator iterator = set.iterator();
                while (iterator.hasNext()) {
                    Integer key = (Integer) iterator.next();
                    if (counts.get(key).equals(max)) {
                        out.writeUTF("block" + "$$" + indexList + "$$" + key);
                        hashStorage.clear();
                        endTime = System.nanoTime();
                        executeTime = endTime - startTime;
                        Runtime.getRuntime().gc();
                        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

                        System.out.println(executeTime + ", " + used);
                    }

                }

            } else {
                secondCheck(data, out);
            }
        }
    }


    private void secondCheck(String data, DataOutputStream out) throws IOException {
        String[] dataSplit = data.split("&&");
        String nodeNumber = dataSplit[1];
        String txData = dataSplit[2];
        String stringTxData = txData.replaceAll("[\\[\\]]", "");
        String[] splitTxData = stringTxData.split(", ");
        HashMap<Integer, Integer> counts = new HashMap<>();

        int min = 0;
        int max = 0;

        for (int i = 0; i < splitTxData.length; i++) {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(splitTxData[i]);
            String jData = element.getAsJsonObject().get("data").getAsString();
            String jIndex = element.getAsJsonObject().get("index").getAsString();

            indexList.add(jIndex);
            dataList.add(jData);

        }
        indexMap.put(nodeNumber, indexList.size());
        dataMap.put(nodeNumber, dataList);

        Collection values = indexMap.values();
        min = (int) Collections.min(values);
        max = (int) Collections.max(values);


        if (min != max){
            List<String> getList = dataList.subList(0, min);
            int hash = getList.hashCode();
            hashStorage2.put(nodeNumber, hash);

            if (hashStorage2.size() == nodeAmount){
                for (Integer c: hashStorage2.values()){
                    int value = counts.get(c) == null ? 0: counts.get(c);
                    counts.put(c, value+1);
                }

                //int hashMax = Collections.max(counts.values());
                int hashMax = 0;
                int bft = nodeAmount - ((nodeAmount - 1) / 3);

                if(hashMax >= bft){
                    out.writeUTF("block" + "$$" + hash);
                    getList.clear();
                    indexList.clear();
                    hashStorage2.clear();

                    endTime = System.nanoTime();
                    executeTime = endTime-startTime;
                    Runtime.getRuntime().gc();
                    long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

                    System.out.println(executeTime+", "+used);
                }
                else {
                    thirdCheck(data, out);
                }
            }
        }

        if (min == max){
            Set<Map.Entry<String, List<String>>> entries = dataMap.entrySet();
            for(Map.Entry<String, List<String>> entry: entries){

                for (int i=0; i<entry.getValue().size(); i++){
                    int dataCount = Collections.frequency(entry.getValue(), entry.getValue().get(i));
                    dataAmount.put(String.valueOf(entry.getValue()), dataCount);
                }
            }

            int bft = dataMap.size() - ((dataMap.size() -1) /3);
            Set<Map.Entry<String, Integer>> entries2 = dataAmount.entrySet();
            for(Map.Entry<String, Integer> entry2: entries2){
                int temp= 0;
                if (temp >= bft){
                    //if(entry2.getValue() >= bft){
                    sendList.add(entry2.getKey());
                    out.writeUTF("block" + "$$" + sendList.hashCode());
                    sendList.clear();
                    endTime = System.nanoTime();
                    executeTime = endTime-startTime;
                    Runtime.getRuntime().gc();
                    long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

                    System.out.println(executeTime+", "+used);
                }
                else {
                    thirdCheck(data, out);
                }

            }

        }

    }

    private void thirdCheck(String data, DataOutputStream out) throws IOException {
        String[] dataSplit = data.split("&&");
        String txData = dataSplit[2];
        if (!dataList2.contains(txData)){
            dataList2.add(txData);
        }
        int hash = dataList2.hashCode();
        out.writeUTF("vote"+hash);
    }

    private void votingCheck(String data, DataOutputStream out) throws IOException {
        int countTrue = 0;
        int countData = 0;

        String[] voteResult = data.split("=");
        voteList.add(voteResult[1]);

        if (voteList.size() == nodeAmount) {
            countTrue = Collections.frequency(voteList, "true");
            countData = voteList.size();
            int bft = countData - ((countData - 1) / 3);

            if (countTrue >= bft) {
                out.writeUTF("block" + "$$" + voteList.hashCode());
                voteList.clear();

                endTime = System.nanoTime();
                executeTime = endTime-startTime;
                Runtime.getRuntime().gc();
                long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                System.out.println("end time=" +endTime);
                System.out.println("m=" +used);
                System.out.println("executeTime="+executeTime);
            } else {
                System.out.println("next Round");
            }

        }
    }


}
