package ru.lovenek0.network.PingPongUDP.server;

import ru.lovenek0.network.PingPongUDP.UDPPacket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class UDPServer {
    private DatagramSocket socket;

    private final ConcurrentHashMap<String, UDPConnection> connections = new ConcurrentHashMap<>();

    private String host;
    private int port;

    private UDPServerListener listener = null;

    private boolean isWork = false;

    private int packetSize;
    private int disconnectTimeout;
    private int receiveTimeout;

    private boolean async;

    public UDPServer() {
        this.packetSize = 1024;
        this.disconnectTimeout = 5000;
        this.receiveTimeout = 200;
        this.async = false;
        bind("127.0.0.1", 20019);
    }
    public UDPServer(String host, int port) {
        this.packetSize = 1024;
        this.disconnectTimeout = 5000;
        this.receiveTimeout = 200;
        this.async = false;
        bind(host, port);
    }
    public UDPServer(String host, int port, int packetSize, int disconnectTimeout, int receiveTimeout){
        this.packetSize = packetSize;
        this.disconnectTimeout = disconnectTimeout;
        this.receiveTimeout = receiveTimeout;
        this.async = false;
        bind(host, port);
    }

    public void bind(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void start(){
        try {
            this.connections.clear();
            this.socket = new DatagramSocket(new InetSocketAddress(host, port));
            this.socket.setSoTimeout(receiveTimeout);
            this.isWork = true;
            if(async)
                (new Thread(this::startListener)).start();
            else
                startListener();
        }
        catch (Exception e){
            if(listener != null)
                listener.OnFailedToStartEvent();
        }
    }
    public void stop(){
        isWork = false;
        for(UDPConnection connection : getConnections()) {
            connection.disconnect();
            sendPacket(connection);
        }
        listener.OnServerStopEvent();
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
    public void setDisconnectTimeout(int time){
        this.disconnectTimeout = time;
    }
    public void setReceiveTimeout(int time){
        this.receiveTimeout = time;
    }

    public boolean isAsync(){
        return async;
    }
    public void setAsync(boolean status){
        this.async = status;
    }

    public void setListener(UDPServerListener listener){
        this.listener = listener;
    }

    protected void sendPacket(UDPConnection connection, byte[] bytes) {
        int size = Math.min(bytes.length, this.packetSize - 1);
        byte[] sendBytes = new byte[size + 1];
        System.arraycopy(bytes, 0, sendBytes, 0, size);
        sendBytes[size] = '\0';
        try {
            DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getByName(connection.getHost()), connection.getPort());
            socket.send(sendPacket);
            if(listener != null)
                listener.OnPacketSendEvent(connection, new UDPPacket(sendBytes));
        } catch (IOException e) {
            if(listener != null)
                listener.OnPacketNotSendEvent(connection, new UDPPacket(sendBytes));
        }
    }
    protected void sendPacket(UDPConnection connection, UDPPacket packet){
        sendPacket(connection, packet.toBytes());
    }
    protected void sendPacket(UDPConnection connection){
        sendPacket(connection, connection.getPacket());
    }

    public UDPConnection[] getConnections(){
        UDPConnection[] list = new UDPConnection[connections.size()];
        int index = 0;
        for(String host : connections.keySet())
            list[index++] = this.connections.get(host);
        return list;
    }

    private void startListener(){
        while (this.isWork){
            connections.forEach((key, connection) -> {
                if (disconnectTimeout > 0 && System.currentTimeMillis() - connection.getLastReceiveTime() >= disconnectTimeout) {
                    if(listener != null)
                        listener.OnDisconnectTimeOutEvent(connection);
                    connections.remove(key);
                }
            });
            try {
                DatagramPacket receivePacket = new DatagramPacket(new byte[packetSize], packetSize);
                socket.receive(receivePacket);
                UDPConnection connection = this.connections.getOrDefault(receivePacket.getSocketAddress().toString().substring(1), null);
                if(connection == null) {
                    connection = new UDPConnection(receivePacket.getAddress().toString().substring(1), receivePacket.getPort());
                    connections.put(connection.getAddress(), connection);
                    if(listener != null)
                        listener.OnConnectEvent(connection);
                }
                UDPPacket packet = new UDPPacket(receivePacket.getData());

                if(packet.GetID() < 0){
                    if(packet.GetID() == -1)
                        if(listener != null)
                            listener.OnServerDroppedConnectionEvent(connection, packet);
                    if(packet.GetID() == -2)
                        if(listener != null)
                            listener.OnClientDroppedConnectionEvent(connection, packet);
                    for(String key : connections.keySet())
                        if(connections.get(key).getAddress().equals(connection.getAddress())) {
                            connections.remove(key);
                            break;
                        }
                }else {
                    if (packet.GetID() > connection.getClientPacketID()) {
                        connection.setClientPacketID(packet.GetID());
                        if(listener != null)
                            listener.OnPacketReceivedEvent(connection, packet);
                        connection.lastReceiveTime = System.currentTimeMillis();
                    } else
                        if(listener != null)
                            listener.OnLatePacketReceivedEvent(connection, packet);
                    sendPacket(connection);
                }
            } catch (IOException e) {
                if(listener != null)
                    listener.OnPacketReceiveTimedOutEvent();
            }
        }
    }
}
