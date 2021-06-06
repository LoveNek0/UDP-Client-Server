package ru.lovenek0;

import java.net.DatagramPacket;

public interface UDPServerListener {
    void OnConnect(UDPConnection connection);
    void OnPacketReceive(UDPConnection connection, UDPPacket packet);
    void OnDisconnectTimeout(UDPConnection connection);
    void OnPacketNotSend(UDPConnection connection, UDPPacket packet);
    void OnPacketSend(UDPConnection connection, UDPPacket packet);
    void OnPacketReceiveTimeout();
    void OnPacketArrivedLate(UDPConnection connection, UDPPacket packet);
    void OnServerDisconnect(UDPConnection connection, UDPPacket packet);
    void OnClientDisconnect(UDPConnection connection, UDPPacket packet);
    void OnDatagramReceive(UDPConnection connection, DatagramPacket packet);
    void OnFailedToStart(Exception e, String host, int port);
}
