import Node.NListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Stemp {


    public static void main(String[] args) {
        ServerSocket ss;
        Socket s;
        DataOutputStream out;
        DataInputStream in;


        try{
            ss = new ServerSocket(8888);
            while (true){
                s = ss.accept();
                out = new DataOutputStream(s.getOutputStream());
                in = new DataInputStream(s.getInputStream());
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
