package ru.lovenek0.network.socket;

import java.io.IOException;
import java.net.*;

public class UDPClient {
    private DatagramSocket socket;

    private String nextData = null;

    private int maxPacketSize = 1024;

    private long clientPacketID = 0;
    private long serverPacketID = 0;

    private long disconnectTimeout = 5000;
    private int receiveTimeout = 200;

    private int lostPackets = 0;

    private UDPClientListener udpClientListener = new UDPClientListener() {
        @Override
        public void OnPacketNotSend(UDPPacket packet) {

        }

        @Override
        public void OnPacketSend(UDPPacket packet) {

        }

        @Override
        public void OnPacketReceiveTimeout() {

        }

        @Override
        public void OnPacketReceive(UDPPacket packet) {

        }

        @Override
        public void OnPacketArrivedLate(UDPPacket packet) {

        }

        @Override
        public void OnDisconnectTimeout() {

        }

        @Override
        public void OnClientDisconnect() {

        }

        @Override
        public void OnServerDisconnect() {

        }

        @Override
        public void OnDatagramReceive(DatagramPacket datagramPacket) {

        }

        @Override
        public void OnConnect() {

        }

        @Override
        public void OnFailedToConnect(Exception e, String host, int port) {

        }
    };

    private long lastReceiveTime = 0;

    private boolean connected = false;

    public UDPClient() {
        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void Connect(String host, int port) {
        try {
            socket.setSoTimeout(receiveTimeout);
            socket.connect(InetAddress.getByName(host), port);
            connected = true;
            lastReceiveTime = 0;
            StartListener();
            udpClientListener.OnConnect();
        }
        catch (Exception e) {
            udpClientListener.OnFailedToConnect(e, host, port);
        }
    }
    public void Disconnect(){
        connected = false;
        this.SendPacket(new UDPPacket(-2, ""));
        socket.disconnect();
        udpClientListener.OnClientDisconnect();
    }

    public void SendData(String data){
        this.nextData = data;
    }

    public void SetMaxPacketSize(int size){
        this.maxPacketSize = size;
    }
    public int GetMaxPacketSize(){
        return this.maxPacketSize;
    }


    public void SetListener(UDPClientListener callbacks){
        udpClientListener = callbacks;
    }

    public void SendPacket(byte[] bytes) {
        int size = Math.min(bytes.length, this.maxPacketSize - 1);
        byte[] sendBytes = new byte[size + 1];
        System.arraycopy(bytes, 0, sendBytes, 0, size);
        sendBytes[size] = '\0';
        DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length);
        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            udpClientListener.OnPacketNotSend(new UDPPacket(bytes));
        }
    }
    public void SendPacket(UDPPacket packet) {
        SendPacket(packet.toBytes());
    }

    public void SetDisconnectTimeout(long timeoutMS){
        disconnectTimeout = timeoutMS;
    }
    public long GetDisconnectTimeout(){
        return disconnectTimeout;
    }

    public void SetReceiveTimeout(int timeoutMS){
        receiveTimeout = timeoutMS;
    }
    public int GetReceiveTimeout(){
        return receiveTimeout;
    }

    public int GetPing(){
        return (int) (System.currentTimeMillis() - lastReceiveTime);
    }
    public int GetLost(){
        return lostPackets - 1;
    }

    private void StartListener(){
        (new Thread(() ->{
            while(connected){
                if(lastReceiveTime == 0)
                    lastReceiveTime = System.currentTimeMillis();
                if(System.currentTimeMillis() - lastReceiveTime >= disconnectTimeout){
                    socket.disconnect();
                    connected = false;
                    udpClientListener.OnDisconnectTimeout();
                    break;
                }
                else {
                    UDPPacket packet = new UDPPacket(++clientPacketID, nextData);
                    SendPacket(packet);
                    nextData = "";
                    udpClientListener.OnPacketSend(packet);
                    try {
                        DatagramPacket receivePacket = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);
                        socket.receive(receivePacket);
                        udpClientListener.OnDatagramReceive(receivePacket);
                        packet = new UDPPacket(receivePacket.getData());

                        if(packet.GetID() == -1){
                            this.SendPacket(new UDPPacket(-1, ""));
                            connected = false;
                            socket.disconnect();
                            udpClientListener.OnServerDisconnect();
                            break;
                        }

                        if (packet.GetID() > serverPacketID) {
                            lostPackets = (int) (packet.GetID() - serverPacketID);
                            serverPacketID = packet.GetID();
                            udpClientListener.OnPacketReceive(packet);
                            lastReceiveTime = System.currentTimeMillis();
                        } else
                            udpClientListener.OnPacketArrivedLate(packet);
                    } catch (IOException e) {
                        udpClientListener.OnPacketReceiveTimeout();
                    }
                }
            }
        })).start();
    }
}