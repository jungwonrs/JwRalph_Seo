package Client;

import Key.KeyGenerator;
import Key.SigGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ClListener {

    private Long startTime;
    private Long endTime;
    private int count = 0;


    public void messageHandler(String data, DataOutputStream out){
        if (data.equals("s")){
            Timer timer = new Timer();
            TimerTask t = new TimerTask() {
                @Override
                public void run() {
                    try{
                        //start time
                        startTime = System.nanoTime();
                        out.writeUTF("send_to_primary="+txGenerator());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            timer.schedule(t, 0, 10000);
        }

        if (data.equals("block")){
            endTime = System.nanoTime();
            count += 1;
            Long executeTime = (endTime-startTime);
            fileOutput(executeTime);

            System.out.println(count);
        }

    }

    private String txGenerator() throws  Exception{
        Random random = new Random();
        String msg;
        String pubKey;
        String sig;

        KeyGenerator key = new KeyGenerator();
        KeyPair getKey = key.genKey();
        PublicKey pKey = getKey.getPublic();

        SigGenerator sg = new Key.SigGenerator();

        try{
            msg = String.valueOf(random.nextInt(100));
            pubKey = key.pubKeyToString(pKey);
            sig = sg.genSig(getKey.getPrivate(), msg);

            Gson gson = new Gson();
            JsonObject object = new JsonObject();
            object.addProperty("mgs", msg);
            object.addProperty("pubKey", pubKey);
            object.addProperty("sig", sig);
            String json = gson.toJson(object);

            return json;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    private void fileOutput(Long time){
        try{
            File file = new File ("C:\\Users\\selab\\Desktop\\time.txt");
            FileWriter fileWriter = new FileWriter(file, true);
            fileWriter.write(String.valueOf(time)+"\n");
            fileWriter.flush();
            fileWriter.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}


