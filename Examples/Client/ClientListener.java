package ru.lovenek0.network.socket;

import java.net.DatagramPacket;

public class ClientListener implements UDPClientListener {
    @Override
    public void OnPacketNotSend(UDPPacket packet) {
        System.out.println("Packet not send: " + packet.GetData());
    }

    @Override
    public void OnPacketSend(UDPPacket packet) {
        // System.out.println("Packet send: " + packet.GetData());
    }

    @Override
    public void OnPacketReceiveTimeout() {
        System.out.println("Receive timeout");
    }

    @Override
    public void OnPacketReceive(UDPPacket packet) {
        if(packet.GetData().length() > 0)
            System.out.println("Packet receive: " + packet.GetData());
    }

    @Override
    public void OnPacketArrivedLate(UDPPacket packet) {
        System.out.println("Packet arrived later: " + packet.GetData());
    }

    @Override
    public void OnDisconnectTimeout() {
        System.out.println("Disconnected: Timeout");
    }

    @Override
    public void OnClientDisconnect() {
        System.out.println("Disconnected by client");
    }

    @Override
    public void OnServerDisconnect() {
        System.out.println("Disconnected by server");
    }

    @Override
    public void OnDatagramReceive(DatagramPacket datagramPacket) {
        //System.out.println("Datagram was received");
    }

    @Override
    public void OnConnect() {
        System.out.println("Connected!");
    }

    @Override
    public void OnFailedToConnect(Exception e, String host, int port) {
        System.out.println("Can't connect to " + host + ":" + port);
    }
}
