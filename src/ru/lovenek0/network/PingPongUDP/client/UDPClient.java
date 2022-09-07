package ru.lovenek0.network.PingPongUDP.client;

import ru.lovenek0.network.PingPongUDP.UDPPacket;

import java.io.IOException;
import java.net.*;

public class UDPClient {
    private DatagramSocket socket = null;

    private String toSend = "";

    private long lastClientPacketID = 0;
    private long lastServerPacketID = 0;

    private long lastReceiveTime = 0;

    private boolean connected = false;

    private UDPClientListener listener = null;

    private int lost = 0;

    private int packetSize;
    private int disconnectTimeout;
    private int receiveTimeout;

    public UDPClient(){
        this.packetSize = 1024;
        this.disconnectTimeout = 5000;
        this.receiveTimeout = 200;
    }
    public UDPClient(int packetSize, int disconnectTimeout, int receiveTimeout){
        this.packetSize = packetSize;
        this.disconnectTimeout = disconnectTimeout;
        this.receiveTimeout = receiveTimeout;
    }

    public boolean connect(String host, int port){
        try {
            this.socket = new DatagramSocket();
            this.socket.setSoTimeout(receiveTimeout);
            this.socket.connect(InetAddress.getByName(host), port);
            this.connected = true;
            this.lastReceiveTime = 0;
            this.lastClientPacketID = 0;
            this.lastServerPacketID = 0;
            this.toSend = "";
            startListener();
            return true;
        } catch (SocketException e) {
            if(listener != null)
                listener.OnFailedToConnectEvent(host, port);
        } catch (UnknownHostException e) {
            if(listener != null)
                listener.OnIncorrectHostnameEvent(host, port);
        }
        return false;
    }
    public void disconnect(){
        connected = false;
        this.sendPacket(new UDPPacket(-2, toSend));
        socket.disconnect();
        if(listener != null)
            listener.OnClientDroppedConnectionEvent();
    }

    public void sendData(String data){
        this.toSend = data;
    }

    public void sendPacket(byte[] bytes) {
        int size = Math.min(bytes.length, this.packetSize - 1);
        byte[] sendBytes = new byte[size + 1];
        System.arraycopy(bytes, 0, sendBytes, 0, size);
        sendBytes[size] = '\0';
        DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            if(listener != null)
                listener.OnPacketNotSendEvent(new UDPPacket(bytes));
        }
    }
    public void sendPacket(UDPPacket packet) {
        sendPacket(packet.toBytes());
    }

    public void setListener(UDPClientListener listener){
        this.listener = listener;
    }

    public int getLost(){
        return lost;
    }
    public int getPing(){
        return (int) (System.currentTimeMillis() - lastReceiveTime);
    }

    public boolean isConnected(){
        return connected;
    }
    public int getPacketSize(){
        return packetSize;
    }
    public int getDisconnectTimeout(){
        return disconnectTimeout;
    }
    public int getReceiveTimeout(){
        return receiveTimeout;
    }
    public void setPacketSize(int size){
        this.packetSize = size;
    }
    public void setDisconnectTimeout(int timeout){
        this.disconnectTimeout = timeout;
    }
    public void setReceiveTimeout(int timeout){
        this.receiveTimeout = timeout;
    }

    private void startListener(){
        (new Thread(() ->{
            while(this.connected){
                if(this.lastReceiveTime == 0)
                    this.lastReceiveTime = System.currentTimeMillis();
                if(disconnectTimeout > 0 && System.currentTimeMillis() - lastReceiveTime >= disconnectTimeout){
                    this.socket.disconnect();
                    this.connected = false;
                    if(listener != null)
                        this.listener.OnDisconnectTimeOutEvent();
                    break;
                }
                else {
                    UDPPacket packet = new UDPPacket(++this.lastClientPacketID, this.toSend);
                    sendPacket(packet);
                    this.toSend = "";
                    if(listener != null)
                        this.listener.OnPacketSendEvent(packet);
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(new byte[this.packetSize], this.packetSize);
                        this.socket.receive(receivePacket);
                        packet = new UDPPacket(receivePacket.getData());
                        if(packet.GetID() == -1){
                            this.sendPacket(new UDPPacket(-1, toSend));
                            this.connected = false;
                            this.socket.disconnect();
                            if(listener != null)
                                this.listener.OnServerDroppedConnectionEvent();
                            break;
                        }

                        if (packet.GetID() > this.lastServerPacketID) {
                            this.lost = (int) (packet.GetID() - this.lastServerPacketID);
                            this.lastServerPacketID = packet.GetID();
                            lastReceiveTime = System.currentTimeMillis();
                            if(listener != null)
                                listener.OnPacketReceivedEvent(packet);
                        } else
                            if(listener != null)
                                listener.OnLatePacketReceivedEvent(packet);
                    } catch (IOException e) {
                        if(listener != null)
                            listener.OnPacketReceiveTimedOutEvent();
                    }
                }
            }
        })).start();
    }
}
