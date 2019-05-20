package client;

import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import packet.ExamplePacket;
import packet.Game;
import packet.Packet;

public class Client 
{
	public static void main(String[] args)
	{
		try {
			// 1. 서버에 접속하기 
			Socket server = new Socket("127.0.0.1",9099);
			
			// 2. Packet을 만들어줌
			//Packet packet = new ExamplePacket("sissor");
            String msg;
            Scanner scan = new Scanner(System.in);
            System.out.println("가위,바위,보 셋중 하나를 입력하세요");
            msg = scan.nextLine();

			Packet packet = new Game(msg);
			
			// 3. 서버에게 packet을 전송해줌
			OutputStream out = server.getOutputStream();
			out.write(packet.getBytes());
			
			// 4. 서버에게서 데이터 받기
			while(true)
			{
				// ~~~
			}
		}catch(Exception e) {
			
		}
	}
}
