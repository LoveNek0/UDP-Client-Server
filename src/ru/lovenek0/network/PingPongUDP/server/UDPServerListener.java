package ru.lovenek0.network.PingPongUDP.server;

import ru.lovenek0.network.PingPongUDP.UDPPacket;

public interface UDPServerListener {
    void OnFailedToStartEvent();
    void OnPacketSendEvent(UDPConnection connection, UDPPacket packet);
    void OnPacketNotSendEvent(UDPConnection connection, UDPPacket packet);
    void OnDisconnectTimeOutEvent(UDPConnection connection);
    void OnConnectEvent(UDPConnection connection);
    void OnServerDroppedConnectionEvent(UDPConnection connection, UDPPacket packet);
    void OnClientDroppedConnectionEvent(UDPConnection connection, UDPPacket packet);
    void OnPacketReceivedEvent(UDPConnection connection, UDPPacket packet);
    void OnLatePacketReceivedEvent(UDPConnection connection, UDPPacket packet);
    void OnPacketReceiveTimedOutEvent();
    void OnServerStopEvent();
}
