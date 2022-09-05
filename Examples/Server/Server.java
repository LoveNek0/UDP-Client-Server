package ru.lovenek0.network.PingPongUDP;

import java.util.Scanner;

public class Server {
    private static final String host = "192.168.1.2";
    private static final int port = 25565;
    public static void Main(){
        UDPServer server = new UDPServer();
        server.Bind(host, port);
        server.SetListener(new ServerListener());
        server.SetReceiveTimeout(200);
        server.SetDisconnectTimeout(5000);
        server.Run();
        while(true) {
            System.out.print("Input: ");
            String line = (new Scanner(System.in)).nextLine();
            for (UDPConnection connection : server.GetConnections())
                connection.SendData(line);
        }
    }
}
