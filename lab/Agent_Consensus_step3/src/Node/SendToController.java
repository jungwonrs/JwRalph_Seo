package Node;

import Key.KeyGenerator;
import Key.SigGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Random;

public class SendToController {


    public String txGenerator() throws Exception{
        Random random = new Random();
        String msg;
        String time;
        String pubKey;
        String sig;

        KeyGenerator key = new KeyGenerator();
        KeyPair getKey = key.genKey();
        PublicKey pkey = getKey.getPublic();

       SigGenerator sg = new Key.SigGenerator();

        try {
            msg = String.valueOf(random.nextInt());
            time = String.valueOf(Instant.now().getEpochSecond());
            pubKey =key.pubKeyToString(pkey);
            sig = sg.genSig(getKey.getPrivate(), msg);

            Gson gson = new Gson();
            JsonObject object = new JsonObject();
            object.addProperty("msg", msg);
            object.addProperty("time", time);
            object.addProperty("pubKey", pubKey);
            object.addProperty("sig", sig);
            String json = gson.toJson(object);

            return json;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String agentStart(String nodeNumber){
        try{
            KeyGenerator key = new KeyGenerator();
            KeyPair getKey = key.genKey();
            PublicKey pKey = getKey.getPublic();
            SigGenerator sg = new SigGenerator();
            String msg = nodeNumber;
            String pubkey = key.pubKeyToString(pKey);
            String sig = sg.genSig(getKey.getPrivate(), msg);

            Gson gson = new Gson();
            JsonObject object = new JsonObject();
            object.addProperty("nodeNumber", nodeNumber);
            object.addProperty("pubKey", pubkey);
            object.addProperty("sig", sig);
            String json = gson.toJson(object);

            return json;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
