package AgentPac;

import NodePac.ServerBack;


public class InitAgent extends ServerBack {

    public void agentOn(){
        sendTX("hello-world");
    }



}
