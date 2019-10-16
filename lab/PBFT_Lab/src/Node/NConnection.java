package Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NConnection {
    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;
    private NListener listen = new NListener();

    public void connect(){
        try{
            s = new Socket("163.239.200.192", 8888);
            out = new DataOutputStream(s.getOutputStream());
            in = new DataInputStream(s.getInputStream());
            while(true){
                listen.getMessage(in.readUTF(), out);
            }

        } catch (IOException e){
            e.printStackTrace();
        }



    }




}
