package server;

public class Server 
{
	public static void main(String[] args) 
	{
		System.out.println("서버 시작.");

		// singleton pattern
		Receiver.getInstance();
	}
}