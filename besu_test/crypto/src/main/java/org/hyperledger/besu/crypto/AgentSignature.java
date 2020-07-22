package org.hyperledger.besu.crypto;


import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;
public class AgentSignature {

    public String genSig(final PrivateKey priKey, final String msg) throws Exception{
        Signature priSig = Signature.getInstance("SHA256withRSA");
        priSig.initSign(priKey);
        priSig.update(msg.getBytes(UTF_8));
        byte[] signature = priSig.sign();

        return Base64.getEncoder().encodeToString(signature);
    }


    public boolean verify (final String msg, final String sig, final PublicKey pubKey) throws Exception{
        Signature pubSig = Signature.getInstance("SHA256withRSA");
        pubSig.initVerify(pubKey);
        pubSig.update(msg.getBytes(UTF_8));

        byte[] signature = Base64.getDecoder().decode(sig);

        return pubSig.verify(signature);
    }

}
