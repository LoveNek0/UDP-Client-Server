package ru.lovenek0.network.socket;

public class UDPConnection {
    private final String host;
    private final int port;

    private long clientPacketID = 0;
    private long serverPacketID = 0;

    private long lastReceiveTime = 0;

    private String nextData = "";

    private int lostPackets = 0;

    public UDPConnection(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void SendData(String data){
        nextData = data;
    }

    public UDPPacket GetPacket(){
        UDPPacket packet = null;
        if(serverPacketID < 0)
            packet = new UDPPacket(serverPacketID, nextData);
        else
            packet = new UDPPacket(++serverPacketID, nextData);
        nextData = "";
        return packet;
    }

    public void Disconnect(){
        serverPacketID = -1;
    }

    public long GetClientPacketID(){
        return clientPacketID;
    }
    public void SetClientPacketID(long id){
        lostPackets = (int) (id - clientPacketID);
        clientPacketID = id;
    }

    public String GetHost(){
        return host;
    }
    public int GetPort(){
        return port;
    }

    public String GetAddress(){
        return host + ":" + port;
    }

    public long GetLastReceiveTime() {
        return lastReceiveTime;
    }
    public void SetLastReceiveTime(long lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }

    public int GetPing(){
        return (int) (System.currentTimeMillis() - lastReceiveTime);
    }

    public int GetLost(){
        return lostPackets - 1;
    }
}
