package packet;

public class Game extends Packet {
    @Override
    public byte[] getBytes() {
        return new byte[0];
    }

    public Game (String msg){
        while(true) {
            System.out.println(msg);

        }
    }
    }

