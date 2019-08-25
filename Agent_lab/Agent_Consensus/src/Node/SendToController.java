package Node;

import Key.KeyGenerator;
import Key.SigGenerator;

import java.io.IOException;
import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.util.Random;

public class SendToController {


    public String txGenerator() throws Exception{
        Random random = new Random();
        String tx;
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

            tx = "{msg==/"+msg+",/time==/"+time+",/pubKey==/"+pubKey+",/sig==/"+sig+"}";
            return tx;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
