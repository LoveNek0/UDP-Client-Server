package ru.lovenek0.network.socket;

import java.net.DatagramPacket;

public class ServerListener implements UDPServerListener{
    @Override
    public void OnConnect(UDPConnection connection) {
        System.out.println("Connected: " + connection.GetAddress());
    }

    @Override
    public void OnPacketReceive(UDPConnection connection, UDPPacket packet) {
        if(packet.GetData().length() > 0) {
            System.out.println("Packet receive from: " + connection.GetAddress());
            System.out.println("Data: " + packet.GetData());
        }
    }

    @Override
    public void OnDisconnectTimeout(UDPConnection connection) {
        System.out.println("Disconnected [Timeout]: " + connection.GetAddress());
    }

    @Override
    public void OnPacketNotSend(UDPConnection connection, UDPPacket packet) {
        System.out.println("Packet not send to " + connection.GetAddress());
    }

    @Override
    public void OnPacketSend(UDPConnection connection, UDPPacket packet) {
        // System.out.println("Packet send to: " + connection.GetAddress());
        // System.out.println("Data: " + packet.GetData());
    }

    @Override
    public void OnPacketReceiveTimeout() {
        System.out.println("Receive timeout");
    }

    @Override
    public void OnPacketArrivedLate(UDPConnection connection, UDPPacket packet) {
        System.out.println("Packet arrived later from: " + connection.GetAddress());
        System.out.println("Data: " + packet.GetData());
    }

    @Override
    public void OnServerDisconnect(UDPConnection connection, UDPPacket packet) {
        System.out.println("Client " + connection.GetAddress() + " was disconnected by server");
    }

    @Override
    public void OnClientDisconnect(UDPConnection connection, UDPPacket packet) {
        System.out.println("Client " + connection.GetAddress() + " was disconnected");
    }

    @Override
    public void OnDatagramReceive(UDPConnection connection, DatagramPacket packet) {
        // System.out.println("Datagram was received from: " + connection.GetAddress());
    }

    @Override
    public void OnFailedToStart(Exception e, String host, int port) {
        System.out.println("Failed to start server on " + host + ":" + port);
        System.out.println(e.getMessage());
    }
}
