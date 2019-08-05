package AgentPac;

import NodePac.ServerBack;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Random;

public class Agent {

    public void agentOn() {
        System.out.println("hello world!");
    }

    public static void main(String [] args){
        ServerBack sb = new ServerBack();
        //sb.nodeMap

    }

}
//    public String randomSeed(){
//        ServerBack sb = new ServerBack();
//        System.out.println(sb.nodeMap.size());
////        long hSeed = seed.hashCode();
////        Random rand = new Random();
////        rand.setSeed(hSeed);
////        rand.nextInt();
