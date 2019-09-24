package Agent;

import java.util.Random;

public class RandomSeed {

    public static String randValue(String seed) {
        String result;

        if (seed.equals("a")) {
            result = "1";
        } else {
            Random rand = new Random();
            int nSeed = seed.hashCode();

            rand.setSeed(nSeed);

            //node 수에 따른 범위 지정해줘야함
            result = String.valueOf(rand.nextInt(100) + 1);
        }
        return result;
    }


}
