package Crypto;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;



public class Key {
    public KeyPair genKey() throws NoSuchAlgorithmException{
        SecureRandom sr = new SecureRandom();
        KeyPairGenerator gen;

        gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048, sr);

        KeyPair kp = gen.genKeyPair();
        return kp;
    }

    public String priKeyToString(PrivateKey pKey) throws  Exception{
        return new String(byteArrayToHex(pKey.getEncoded()));
    }


    public String pubKeyToString(PublicKey pubKey) throws  Exception{
        return new String(byteArrayToHex(pubKey.getEncoded()));
    }



    public PublicKey stringToPublicKey(String pubKey) throws Exception{
        X509EncodedKeySpec uKeySpec = new X509EncodedKeySpec(hexToByteArray(pubKey));
        KeyFactory uKeyFactor = KeyFactory.getInstance("RSA");
        PublicKey uKey;
        uKey = uKeyFactor.generatePublic(uKeySpec);
        return uKey;

    }

    public static byte[] hexToByteArray(String hex){
        if (hex == null || hex.length() == 0){
            return null;
        }
        byte [] ba = new byte[hex.length() / 2];
        for (int i = 0; i< ba.length; i++){
            ba[i]  = (byte) Integer.parseInt(hex.substring(2*i, 2*i+2), 16 );
        }
        return ba;
    }

    public static String byteArrayToHex(byte[] ba){
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
