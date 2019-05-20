package server;

import java.io.InputStream;
import java.net.Socket;

import packet.ExamplePacket;
import packet.Packet;

public class Sender extends Thread
{
	private Socket socket = null;
	
	public Sender(Socket socket)
	{
		this.socket = socket;
		this.start();
	}
	
	@Override
	public void run()
	{
		try {
			InputStream in = socket.getInputStream();
			while(true)
			{
				if(in.available() > 0)
				{
					// pid 정보 읽기 (8byte)
					int count = 0;
					byte[] data = new byte[8];
					while(count < 8)
					{
						int r = in.read();
						if(r != -1)
						{
							data[count] = (byte)r;
							count++;
						}
					}
					long pid = Packet.bytesToLong(data);
					
					// type 정보 읽기 (4byte)
					count = 0;
					data = new byte[4];
					while(count < 4)
					{
						int r = in.read();
						if(r != -1)
						{
							data[count] = (byte)r;
							count++;
						}
					}
					int type = Packet.byteArrayToInt(data);
					
					// bodylength 정보 읽기 (4byte)
					count = 0;
					data = new byte[4];
					while(count < 4)
					{
						int r = in.read();
						if(r != -1)
						{
							data[count] = (byte)r;
							count++;
						}
					}
					int bodylength = Packet.byteArrayToInt(data);
					
					// body 정보 읽기 (nbyte)
					count = 0;
					data = new byte[bodylength];
					while(count < bodylength)
					{
						int r = in.read();
						if(r != -1)
						{
							data[count] = (byte)r;
							count++;
						}
					}
					
					// 상대가 보낸 byte를 packet으로 변환한다.
					Packet pack = new ExamplePacket(pid,type,bodylength,data);
					
					// 해당 packet에 담겨 있는 메세지를 읽는다.
					System.out.println(pack.toString());
				}
			}
		}catch(Exception e) {
			
		}
		
	}
}
