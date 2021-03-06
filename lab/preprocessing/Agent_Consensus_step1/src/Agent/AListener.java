package Agent;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.*;

public class AListener {
    private HashMap<String, Integer> hashStorage = new HashMap<String, Integer>();
    //network node 수
    private Integer nodeAmount = 68;
    private Long endTime;
    private Long startTime;
    private Long executeTime;
    private int experimentCount = 0;

    public void messageHandler(String data, DataOutputStream out) {
        if (data.contains("Agent start by")) {
            startTime = System.nanoTime();
            //System.out.println("startTime= "+startTime);
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

            int max = Collections.max(counts.values());
            int bft = nodeAmount - ((nodeAmount - 1) / 3);

            if (max >= bft) {
                Set set = counts.keySet();
                Iterator iterator=set.iterator();


                while(iterator.hasNext()){
                    Integer key = (Integer)iterator.next();
                    if (counts.get(key).equals(max)){
                        out.writeUTF("block" + "$$" + indexList + "$$" + key);
                        hashStorage.clear();
                        endTime = System.nanoTime();
                        //System.out.println("endTime= "+endTime);
                        Runtime.getRuntime().gc();
                        long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
                        executeTime = endTime-startTime;
                        //System.out.println(executeTime+", "+used);
                        fileOutput(executeTime, used);
                        experimentCount += 1;
                        System.out.println(experimentCount);

                    }

                }

            }
        }
    }

    private void fileOutput(Long time, Long used){
        try{
            File file = new File ("C:\\Users\\selab\\Desktop\\time.txt");
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(String.valueOf(time)+"\n");
            fileWriter.flush();
            fileWriter.close();


            File file2 = new File("C:\\Users\\selab\\Desktop\\memory.txt");
            FileWriter fileWriter2 = new FileWriter(file2, true);
            fileWriter2.write(String.valueOf(used)+"\n");
            fileWriter2.flush();
            fileWriter2.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }



    private void secondCheck(HashMap<String, Integer> data){
        System.out.println("second");
    }

}
