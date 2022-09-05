package ru.lovenek0.network.PingPongUDP.server;

import ru.lovenek0.network.PingPongUDP.UDPPacket;

public class UDPConnection {
    private final String host;
    private final int port;

    private long clientPacketID = 0;
    private long serverPacketID = 0;

    protected long lastReceiveTime = 0;

    private String nextData = "";

    private int lostPackets = 0;

    public UDPConnection(String host, int port){
        this.host = host;
        this.port = port;
    }

    public String getHost(){
        return host;
    }
    public int getPort(){
        return port;
    }
    public String getAddress(){
        return host + ":" + port;
    }

    public int getPing(){
        return (int) (System.currentTimeMillis() - lastReceiveTime);
    }
    public int getLost(){
        return lostPackets - 1;
    }

    public void sendData(String data){
        nextData = data;
    }

    protected UDPPacket getPacket(){
        UDPPacket packet = null;
        if(serverPacketID < 0)
            packet = new UDPPacket(serverPacketID, nextData);
        else
            packet = new UDPPacket(++serverPacketID, nextData);
        nextData = "";
        return packet;
    }

    public void disconnect(){
        serverPacketID = -1;
    }

    protected long getClientPacketID(){
        return clientPacketID;
    }
    protected void setClientPacketID(long id){
        lostPackets = (int) (id - clientPacketID);
        clientPacketID = id;
    }

    protected long getLastReceiveTime() {
        return lastReceiveTime;
    }
}
