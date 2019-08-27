package AgentPac;

import java.util.Random;

public class RandomSeed {

    public static String randValue(String seed){
        Random rand = new Random();
        int nSeed = seed.hashCode();

        rand.setSeed(nSeed);
        String result = String.valueOf(rand.nextInt(1)+1);

        return result;
    }

}
