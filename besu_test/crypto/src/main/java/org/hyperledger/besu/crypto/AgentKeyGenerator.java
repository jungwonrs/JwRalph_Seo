package org.hyperledger.besu.crypto;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


public class AgentKeyGenerator {

    public KeyPair genKey() throws NoSuchAlgorithmException {
        KeyPairGenerator gen;

        gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048, SecureRandomProvider.createSecureRandom());

        KeyPair kp = gen.genKeyPair();
        return kp;
    }

    public String pubKeyToString(final PublicKey pubKey){
        return byteArrayToHex(pubKey.getEncoded());
    }

    public PublicKey stringToPublicKey(final String pubKey) throws Exception{
        X509EncodedKeySpec uKeySpec = new X509EncodedKeySpec(hexToByteArray(pubKey));
        KeyFactory uKeyFactor = KeyFactory.getInstance("RSA");
        PublicKey uKey;
        uKey = uKeyFactor.generatePublic(uKeySpec);
        return uKey;

    }

    @SuppressWarnings("JdkObsolete")
    public static byte[] hexToByteArray(final String hex){
        if (hex == null || hex.length() == 0){
            return null;
        }
        byte [] ba = new byte[hex.length() / 2];
        for (int i = 0; i< ba.length; i++){
            ba[i]  = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16 );
        }
        return ba;
    }

    @SuppressWarnings("JdkObsolete")
    public static String byteArrayToHex(final byte[] ba){
        if (ba == null || ba.length ==0){
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber = "";

        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));

        }
        return sb.toString();
    }


}
