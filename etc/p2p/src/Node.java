import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Node {

    public static void main(String [] args){
        String msg;
        Scanner scan = new Scanner(System.in);

        try{
            Socket sc = new Socket("127.0.0.1", 9099);
            InputStream in = sc.getInputStream();
            DataInputStream dis = new DataInputStream(in);
            System.out.println(dis.readUTF());
            while (true){
                msg = scan.nextLine();
                Packet packet = new GamePacket(msg);
                OutputStream out = sc.getOutputStream();
                out.write(packet.getBytes());


            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
