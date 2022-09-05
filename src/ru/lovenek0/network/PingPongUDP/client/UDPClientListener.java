package ru.lovenek0.network.PingPongUDP.client;

import ru.lovenek0.network.PingPongUDP.UDPPacket;

public interface UDPClientListener {
    void OnIncorrectHostnameEvent(String host, int port);
    void OnFailedToConnectEvent(String host, int port);
    void OnPacketNotSendEvent(UDPPacket packet);
    void OnDisconnectTimeOutEvent();
    void OnPacketSendEvent(UDPPacket packet);
    void OnServerDroppedConnectionEvent();
    void OnClientDroppedConnectionEvent();
    void OnPacketReceivedEvent(UDPPacket packet);
    void OnLatePacketReceivedEvent(UDPPacket packet);
    void OnPacketReceiveTimedOutEvent();
}
