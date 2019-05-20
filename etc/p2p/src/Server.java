import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args){
        ServerSocket server;
        Socket sc;

        try{
            server = new ServerSocket(9099);
            while (true) {
                sc = server.accept();
                System.out.println("Connected: "+ sc.getInetAddress());
                OutputStream out = sc.getOutputStream();
                DataOutputStream dos = new DataOutputStream(out);
                dos.writeUTF("Game Start!");

                new Receive(sc);


                }

            } catch (IOException e1) {
            e1.printStackTrace();
        }




    }


}
