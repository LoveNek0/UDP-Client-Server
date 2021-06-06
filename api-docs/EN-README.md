# UDP Client-Server API
## Description
API for applications using a UDP Ping-Pong connection
## Installation
Add JAR file to project
## Examples
### Server
#### Server creation and launch
```java
// First option (default address and port: 127.0.0.1:20019)
UDPServer server = new UDPServer();
server.Run();

// Second option (address and port when creating an object)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.Run();

// Third option (address and port are replaced before launch)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.Bing("192.168.1.2", 25565);
server.Run();
```
#### Using timeouts
```
// Packet receiving timeout (milliseconds)
// Recommended value 100-200
server.SetReceiveTimeout(200);

// Client disconnect timeout (milliseconds)
server.SetDisconnectTimeout(5000);
```

#### Installing an event handler
The event handler class must implement the interface [UDPServerListener](/src/ru/lovenek0/network/socket/UDPServerListener.java)
```
Listener listener = new Listener();
server.SetListener(listener);
```

#### Actions with clients
```
// Get all clients
ArrayList<UDPConnection> connections = server.GetConnections();

// Get the first client from the list
UDPConnection connection = connections.get(0);

// Send a message to the client
connection.SendData("Hello!");

// Disconnect client
connection.Disconnect();
```

### Client
#### Create client, connect to server and disconnect
```
UDPClient client = new UDPClient();
client.Connect("192.168.1.2", 25565);
client.Disconnect();
```

#### Sending a message to the server
```
client.SendData("hello, buddy!");
```

#### Using timeouts
```
// Packet receiving timeout (milliseconds)
// Recommended value 100-200
client.SetReceiveTimeout(200);

// Timeout for disconnecting from the server (milliseconds)
client.SetDisconnectTimeout(5000);
```

#### Installing an event handler
The event handler class must implement the interface [UDPClientListener](/src/ru/lovenek0/network/socket/UDPClientListener.java)
```
Listener listener = new Listener();
client.SetListener(listener);
```

## Additionally
Author: [LoveNek0](https://t.me/lovenek0)