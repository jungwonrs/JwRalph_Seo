import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class N1temp {

    public static void main(String[] args){
        Socket s;
        DataInputStream in;
        DataOutputStream out;

        try{
            s=new Socket("163.239.200.192",7777);
            out=new DataOutputStream(s.getOutputStream());
            in=new DataInputStream(s.getInputStream());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
