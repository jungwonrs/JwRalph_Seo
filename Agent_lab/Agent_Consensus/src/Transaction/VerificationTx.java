package Transaction;

import Key.KeyGenerator;
import Key.SigGenerator;

import java.security.PublicKey;
import java.util.HashMap;

public class VerificationTx {
    Converter ct = new Converter();

    public String vMessage (String tx) throws Exception{
        String msg;
        String sPubKey;
        String sig;
        PublicKey pubKey;
        boolean mVerify;
        SigGenerator verify = new SigGenerator();
        KeyGenerator key = new KeyGenerator();

        HashMap<String, String> dataMap;
        dataMap = ct.ChangeToMap(tx);

        msg = dataMap.get("msg");
        sPubKey = dataMap.get("pubKey");
        sig = dataMap.get("sig");
        pubKey = key.stringToPublicKey(sPubKey);

        mVerify = verify.verify(msg, sig, pubKey);

        if (mVerify){
            return tx;
        }
        String notVerified = "not Verified";

        return notVerified;
    }


}
