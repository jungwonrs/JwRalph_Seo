package Transaction;

import Key.KeyGenerator;
import Key.SigGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.security.PublicKey;

public class VerificationTx {


    public String vMessage(String tx) throws Exception{
        String msg;
        String sPubKey;
        String sig;
        PublicKey pubKey;
        boolean mVerify;
        SigGenerator verify = new SigGenerator();
        KeyGenerator key = new KeyGenerator();


        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(tx);

        msg = element.getAsJsonObject().get("msg").getAsString();
        sPubKey = element.getAsJsonObject().get("pubKey").getAsString();
        sig = element.getAsJsonObject().get("sig").getAsString();
        pubKey = key.stringToPublicKey(sPubKey);

        mVerify = verify.verify(msg, sig, pubKey);

        if (mVerify){
            return tx;
        }
        String notVerified = "not Verified";

        return notVerified;
    }


}
