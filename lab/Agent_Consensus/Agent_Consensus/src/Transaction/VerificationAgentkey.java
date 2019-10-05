package Transaction;

import Key.KeyGenerator;
import Key.SigGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.security.PublicKey;

public class VerificationAgentkey {

    public String vNodeNumber(String data) throws Exception{
        String nodeNumber;
        String sPubKey;
        String sig;
        PublicKey pubKey;
        boolean agentOn;
        SigGenerator verify = new SigGenerator();
        KeyGenerator key = new KeyGenerator();

        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(data);

        nodeNumber = element.getAsJsonObject().get("nodeNumber").getAsString();
        sPubKey = element.getAsJsonObject().get("pubKey").getAsString();
        sig = element.getAsJsonObject().get("sig").getAsString();
        pubKey = key.stringToPublicKey(sPubKey);

        agentOn = verify.verify(nodeNumber, sig, pubKey);

        if (agentOn){
            return "Agent start by "+nodeNumber;
        }

        return "Agent key node not verified";
    }






}
