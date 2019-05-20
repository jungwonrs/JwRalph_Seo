package packet;

public class ExamplePacket extends Packet 
{
	public ExamplePacket(String msg)
	{
		this.type = EXAMPLE_TYPE;
		this.bodylength = msg.getBytes().length;
		this.body = msg.getBytes();
	}
	
	public ExamplePacket(long pid, int type, int length, byte[] body){
		this.pid = pid;
		this.type = type;
		this.bodylength = length;
		this.body = body;
	}

	@Override 
	public String toString()
	{
		return new String(body);
	}
	
	@Override
	public byte[] getBytes() 
	{
		// 보내게될 변수 형의 크기를 모두 합친 크기만큼 byte[]를 생성함
		byte[] datas = new byte[16 + this.bodylength];	
		
		// System.arraycopy(복사할변수, 시작위치, 복사당하는변수, 시작위치, 마지막위치(변수의크기));
		
		System.arraycopy(longToBytes(pid), 0, datas, 0, 8);
		
		System.arraycopy(intToByteArray(this.type), 0, datas, 8, 4);
		
		System.arraycopy(intToByteArray(this.bodylength), 0, datas, 12, 4);
		
		System.arraycopy(this.body, 0, datas, 16, this.bodylength);
		
		return datas;
	}
}
