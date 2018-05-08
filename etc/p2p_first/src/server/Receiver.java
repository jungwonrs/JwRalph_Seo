package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Receiver extends Thread
{
	private ServerSocket server = null;
	private static Receiver instance = null;
	private Sender client = null;
	
	private Receiver()
	{
		try {
			server = new ServerSocket(9099);
			this.start();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static Receiver getInstance() {
		if(instance == null) {
			instance = new Receiver();
		}
		return instance;
	}
			
	
	@Override
	public void run()
	{
		while(true)
		{
			try {
				client = new Sender(server.accept());
				System.out.println("게임을 시작합니다.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
}
