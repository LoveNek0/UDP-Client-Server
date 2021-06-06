package ru.lovenek0.network.socket;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class UDPServer {
    private DatagramSocket socket;

    private final ConcurrentHashMap<String, UDPConnection> connections = new ConcurrentHashMap<>();

    private UDPServerListener udpServerListener = new UDPServerListener() {
        @Override
        public void OnConnect(UDPConnection connection) {

        }

        @Override
        public void OnPacketReceive(UDPConnection connection, UDPPacket packet) {

        }

        @Override
        public void OnDisconnectTimeout(UDPConnection connection) {

        }

        @Override
        public void OnPacketNotSend(UDPConnection connection, UDPPacket packet) {

        }

        @Override
        public void OnPacketSend(UDPConnection connection, UDPPacket packet) {

        }

        @Override
        public void OnPacketReceiveTimeout() {

        }

        @Override
        public void OnPacketArrivedLate(UDPConnection connection, UDPPacket packet) {

        }

        @Override
        public void OnServerDisconnect(UDPConnection connection, UDPPacket packet) {

        }

        @Override
        public void OnClientDisconnect(UDPConnection connection, UDPPacket packet) {

        }

        @Override
        public void OnDatagramReceive(UDPConnection connection, DatagramPacket packet) {

        }

        @Override
        public void OnFailedToStart(Exception e, String host, int port) {

        }
    };

    private String host;
    private int port;

    private final int maxPacketSize = 1024;

    private long disconnectTimeout = 5000;
    private int receiveTimeout = 200;

    public UDPServer() {
        Bind("127.0.0.1", 20019);
    }
    public UDPServer(String host, int port) {
        Bind(host, port);
    }

    public void Bind(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void Run() {
        try {
            socket = new DatagramSocket(new InetSocketAddress(host, port));
            socket.setSoTimeout(receiveTimeout);
            StartListener();
        }
        catch (Exception e){
            udpServerListener.OnFailedToStart(e, host, port);
        }
    }

    public void SetDisconnectTimeout(long timeoutMS){
        this.disconnectTimeout = timeoutMS;
    }
    public long GetDisconnectTimeout(){
        return disconnectTimeout;
    }

    public void SetReceiveTimeout(int timeoutMS){
        this.receiveTimeout = timeoutMS;
    }
    public int GetReceiveTimeout(){
        return receiveTimeout;
    }

    public void SetListener(UDPServerListener callbacks){
        udpServerListener = callbacks;
    }

    public void SendPacket(UDPConnection connection, byte[] bytes) {
        int size = Math.min(bytes.length, this.maxPacketSize - 1);
        byte[] sendBytes = new byte[size + 1];
        System.arraycopy(bytes, 0, sendBytes, 0, size);
        sendBytes[size] = '\0';
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getByName(connection.GetHost()), connection.GetPort());
            socket.send(sendPacket);
            udpServerListener.OnPacketSend(connection, new UDPPacket(sendBytes));
        } catch (IOException e) {
            udpServerListener.OnPacketNotSend(connection, new UDPPacket(sendBytes));
        }
    }
    public void SendPacket(UDPConnection connection, UDPPacket packet){
        SendPacket(connection, packet.toBytes());
    }
    public void SendPacket(UDPConnection connection){
        SendPacket(connection, connection.GetPacket());
    }

    public ArrayList<UDPConnection> GetConnections(){
        ArrayList<UDPConnection> list = new ArrayList<>();
        for(String key:connections.keySet())
            list.add(connections.get(key));
        return list;
    }

    private void StartListener(){
        (new Thread(()->{
            while (true){
                connections.forEach((key, connection) -> {
                    if (System.currentTimeMillis() - connection.GetLastReceiveTime() >= disconnectTimeout) {
                        udpServerListener.OnDisconnectTimeout(connection);
                        connections.remove(key);
                    }
                });
                try {
                    DatagramPacket receivePacket = new DatagramPacket(new byte[maxPacketSize], maxPacketSize);
                    socket.receive(receivePacket);
                    UDPConnection connection = null;
                    for(String key:connections.keySet())
                        if(connections.get(key).GetAddress().equals(receivePacket.getSocketAddress().toString().substring(1))){
                            connection = connections.get(key);
                            break;
                        }
                    if(connection == null) {
                        connection = new UDPConnection(receivePacket.getAddress().toString().substring(1), receivePacket.getPort());
                        connections.put(connection.GetAddress(), connection);
                        udpServerListener.OnConnect(connection);
                    }

                    udpServerListener.OnDatagramReceive(connection, receivePacket);
                    UDPPacket packet = new UDPPacket(receivePacket.getData());

                    if(packet.GetID() < 0){
                        if(packet.GetID() == -1)
                            udpServerListener.OnServerDisconnect(connection, packet);
                        if(packet.GetID() == -2)
                            udpServerListener.OnClientDisconnect(connection, packet);
                        for(String key : connections.keySet())
                            if(connections.get(key).GetAddress().equals(connection.GetAddress())) {
                                connections.remove(key);
                                break;
                            }
                    }else {
                        if (packet.GetID() > connection.GetClientPacketID()) {
                            connection.SetClientPacketID(packet.GetID());
                            udpServerListener.OnPacketReceive(connection, packet);
                            connection.SetLastReceiveTime(System.currentTimeMillis());
                        } else
                            udpServerListener.OnPacketArrivedLate(connection, packet);
                        SendPacket(connection);
                    }
                } catch (IOException e) {
                    udpServerListener.OnPacketReceiveTimeout();
                }
            }
        })).start();
    }
}
