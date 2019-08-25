package Transaction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

public class TxPool {
    private Converter ct = new Converter();

    public String transaction(String tx, double index, String nodeNumber){

        String StrIndex = String.valueOf(index);
        String key = nodeNumber+"_"+StrIndex;

        HashMap<String, HashMap<String, String>> txForm = new HashMap<>();
        HashMap<String, String> map = ct.txToMap(tx);

        txForm.put(key, map);

        String sTxForm = txForm.toString();
        return sTxForm;

    }

//    public void timeControl(String data, String nodenumber){
//        data = data.substring(1, data.length()-1);
//        String[] keyValueParis = data.split("=");
//        String index = keyValueParis[0];
//        String value = keyValueParis[1]+"="+keyValueParis[2]+"="+keyValueParis[3]+"="+keyValueParis[4]+"="+keyValueParis[5]+"="+keyValueParis[6]+"="+keyValueParis[7];
//
//
//
//
//
//    }
//tx 변형 시켜 JSON to Map MAP to Json 형태로 거기서 부터 시작해야될듯

    public static void main(String[]args){
        String temp = "{1_0.04={msg=-448798197, sig=AAqoTWNS72PfWLceLs9pK8GNdIBi8xXr50eVRGoP16sCbuf6ZiCQhPNwu0qN7yc3jl4l0MpWk5TlP5H3aGXelXgOqs5nnx71XIvHdvmqtukOjJzWXFBz6B8hMbECYNFmxelNzEgyXvVTMS7DUnFaw0kRKXTpSreHlu3oevH9lQAY5qDWG+7OVjvNexAimpQj1/8yNxqJGGMMTyK3oTnjImFtuGZ9sydomwtX9dxn5WTUZnG9AjtvdXjNpEluyOkFGNWVpdo/K03/cDcB7CGJ0KwDS4rOpaSBzwnWQmdQn5Nf8492/DAZraRiW6JG6En6UkqXkA5hQUYgeHEJpOXxxQ==, time=1566726096, pubKey=30820122300d06092a864886f70d01010105000382010f003082010a028201010090aebda0365df46371c46199559c4209bd890232c38c01594a51d4b477abaae703bb1af2440e06a842a77a92df9d7d8e6d50daf659e0b904a7778c4f4412067f968fb0f3beffe104d9000deedd30c5028e061d72a6a0899a434af29c3c421c629f9c45adad5bdeda8f345c0dd99e9372daebe790e2a230e4d16db8114557f388e7a0e1cf7cc9a46f5e00b69d010a3a7d09ab58969b554e0b44940b4c55f0dc6cbd6bc33e234a10be884c2ab655bd5c4c4a2cc8899c95012f8c912a187c3c49cd3d557703686d256f8d852f65168630b69ff97c664daa4be7e5a81ced9e407d50754484248ad2ff533a3a75ded0d804721c19d968253a4c5bcb3b99e03d62f2850203010001}}";
        temp = temp.substring(1, temp.length()-1);
        String[] keyValueParis = temp.split("=");
        HashMap<String, String> map = new HashMap<>();

        String temp2 = keyValueParis[1]+"="+keyValueParis[2]+"="+keyValueParis[3]+"="+keyValueParis[4]+"="+keyValueParis[5]+"="+keyValueParis[6]+"="+keyValueParis[7];
        temp2 = temp2.substring(1, temp2.length()-1);
        String[] keyVlauePairs2 = temp2.split("=");
        //System.out.println(keyVlauePairs2[1]);
        for (String pair: keyVlauePairs2){
            //System.out.print(pair);
            String[] entry = pair.split(",");
            System.out.print(entry[0].trim());
        }
//
//        for (String pair: keyVlauePairs2){
//            String[] entry = pair.split(",");
//            map.put(entry[0].trim(), entry[1].trim());
//            System.out.println(entry[0]);
//        }




        // System.out.println(temp2);


        //System.out.println(keyValueParis[1]);





//        HashMap<String, String> map = new HashMap<>();
//
//        for (String pair : keyValueParis){
//            String[] entry = pair.split("=");
//            map.put(entry[0].trim());
//        }
//        System.out.println(map);


    }



}
