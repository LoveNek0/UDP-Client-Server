package ru.lovenek0.network.socket;

import java.net.DatagramPacket;

public interface UDPClientListener {
    void OnPacketNotSend(UDPPacket packet);
    void OnPacketSend(UDPPacket packet);
    void OnPacketReceiveTimeout();
    void OnPacketReceive(UDPPacket packet);
    void OnPacketArrivedLate(UDPPacket packet);
    void OnDisconnectTimeout();
    void OnClientDisconnect();
    void OnServerDisconnect();
    void OnDatagramReceive(DatagramPacket datagramPacket);
    void OnConnect();
    void OnFailedToConnect(Exception e, String host, int port);
}
