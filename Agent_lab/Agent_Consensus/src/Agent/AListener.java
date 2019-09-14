package Agent;


import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

public class AListener {
    private HashMap<String, Integer> hashStorage = new HashMap<String, Integer>();
    //network node 수
    private Integer NodeAmount = 1;

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
            String[] dataSplit = data.split("&&");
            String nodeNumber = dataSplit[1];
            String txData = dataSplit[2];
            int hashData = txData.hashCode();

            hashStorage.put(nodeNumber, hashData);
            firstCheck(hashStorage);
        }
    }



    private void firstCheck(HashMap<String, Integer> data) {
        //todo 데이터를 한꺼번에 가져오는게 아니라 노드 번호 나오고 또 나오고 이런식인거 같은데.. {1=12313}, {1=234324,2=3243242} 이런식인거 같은데 이것도 해결해야됨
        System.out.println(data);
        Map<Integer, Integer> counts = new HashMap<>();
        for (Integer c : data.values()) {
            int value = counts.get(c) == null ? 0 : counts.get(c);
            counts.put(c, value + 1);
        }

        int max = Collections.max(counts.values());

        if (max >= NodeAmount - ((NodeAmount - 1) / 3)) {
            for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
                if (entry.getValue().equals(max)) {
                    System.out.println(entry.getKey());
                }
            }
        } else {
            secondCheck(data);
        }

    }

    private void secondCheck(HashMap<String, Integer> data){
        System.out.println("second");
    }


}
