package AgentConnection;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;

public class NodeList {
    private List<Socket> nodeList;
    private DataOutputStream keyNode;
    private int NodeCount;

    public int getNodeCount() {
        return NodeCount;
    }

    public void setNodeCount(int nodeCount) {
        NodeCount = nodeCount;
    }


    public DataOutputStream getKeyNode() {
        return keyNode;
    }

    public void setKeyNode(DataOutputStream keyNode) {
        this.keyNode = keyNode;
    }


    public List<Socket> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Socket> nodeList) {
        this.nodeList = nodeList;
    }



}
