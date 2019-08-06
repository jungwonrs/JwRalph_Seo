package AgentPac;


import NodePac.ServerBack;

public class AgentBack extends ServerBack {
    private Agent gui;

    public void setGui(Agent gui) {
        this.gui = gui;
    }

    //agent algortihm 짜야됨...
    public void temp (String nb){
        gui.appendMsg(nb);
        System.out.println(nb);
        sendTX("nb");
    }
}
