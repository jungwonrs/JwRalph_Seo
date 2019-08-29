package Agent;


import java.io.DataOutputStream;
import java.io.IOException;

public class AListener {

    public void messageHandler(String data, DataOutputStream out) {
        if (data.contains("Agent start by")) {
            try {
                out.writeUTF("Agent on");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
