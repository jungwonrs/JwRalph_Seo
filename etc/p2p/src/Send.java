import java.io.IOException;
import java.net.ServerSocket;

public class Send {
    private ServerSocket sc = null;
    private static Send instance = null;
    private Receive re = null;

    private Send () {
        try {
            sc = new ServerSocket(9099);
            sender();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Send getInstance(){
        if (instance == null){
            instance = new Send();
        }

        return instance;
    }

    private void sender(){
        while(true){
            try{
                re = new Receive(sc.accept());
                System.out.print("connected");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public static void main (String [] args){
        Send.getInstance();


    }



}
