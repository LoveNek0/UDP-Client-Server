package ru.lovenek0.network.socket;

import java.util.Scanner;

public class Client {
    private static final String host = "192.168.1.2";
    private static final int port = 25565;
    public static void Main(){
        UDPClient client = new UDPClient();
        client.SetListener(new ClientListener());
        client.SetReceiveTimeout(200);
        client.SetDisconnectTimeout(5000);
        client.Connect(host, port);
        while(true) {
            System.out.print("Input: ");
            String line = (new Scanner(System.in)).nextLine();
            client.SendData(line);
        }
    }
}
