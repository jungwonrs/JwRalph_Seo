import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Receive extends Thread{
    private Socket socket;

    public Receive(Socket socket){
        this.socket = socket;
        this.start();
    }

    @Override
    public void run() {
        List<String> result = new ArrayList<>();
        try{
            InputStream in = socket.getInputStream();
            while (true){
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
                    Packet pack = new GamePacket(pid,type,bodylength,data);

                    // 해당 packet에 담겨 있는 메세지를 읽는다.

                    if (result.size() < 2){

                        switch (pack.toString()) {
                            case "가위":
                                result.add(pack.toString());
                                break;
                            case "바위":
                                result.add(pack.toString());
                                break;
                            case "보":
                                result.add(pack.toString());
                                break;
                            default:
                                System.out.println("error!");
                                break;

                        }
                        if (result.size() == 2){
                            check(result);
                        }

                    }
                    else {
                        System.out.println("re-type");
                        result.clear();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    private void check(List<String> msg){

        if (msg.get(0).equals("가위")){
            switch (msg.get(1)){
                case "가위":
                    System.out.println("Same!!");
                    break;
                case "바위":
                    System.out.println("Second player win");
                    break;
                case "보":
                    System.out.println("First player win");
                    break;
            }
        }
        else if (msg.get(0).equals("바위")){
            switch (msg.get(1)){
                case "가위":
                    System.out.println("First player win");
                    break;
                case "바위":
                    System.out.println("Same");
                    break;
                case "보":
                    System.out.println("Second player win");
                    break;
            }
        }

        else if (msg.get(0).equals("보")){
            switch (msg.get(1)){
                case "가위":
                    System.out.println("Second player win");
                    break;
                case "바위":
                    System.out.println("First player win");
                    break;
                case "보":
                    System.out.println("Same");
                    break;
            }
        }
    }


}
