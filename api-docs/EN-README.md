# UDP Client-Server API
## Description
API for applications using a multi-client UDP Ping-Pong connection
## Installation
Add a JAR file to a project

### Server
#### Create and start the server
```java
// First option (default address and port: 127.0.0.1:20019)
UDPServer server = new UDPServer();
server.start();

// Second option (address and port when creating an object)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.start();

// The third option (when creating, address, port, packet size,
//                      client wait timeout and packet receive timeout)
UDPServer server = new UDPServer("127.0.0.1", 23344, 2048, 10000, 500);
server.start();
        
        
// Fourth option (address and port are replaced before launch)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.bind("192.168.1.2", 25565);
server.start();
```
#### Using timeouts
```java
// Packet timeout (milliseconds)
// Recommended value 100-200
server.setReceiveTimeout(200);

// Client disconnect timeout (milliseconds)
server.setDisconnectTimeout(5000);
```

#### Installing an event handler
The event handler class must implement the [UDPServerListener](/src/ru/lovenek0/network/PingPongUDP/UDPServerListener.java) interface
```java
Listener listener = new Listener();
server.setListener(listener);
```

#### Actions with clients
```java
// Get all clients
UDPConnection[] connections = server.getConnections();

// Get the first client from the list
UDPConnection connection = connections[0];

// Send a message to the client
connection.sendData("Hello!");

// Disconnect client
connection.disconnect();
```

### Client
#### Create client, connect to server and disconnect
```java
UDPClient client = new UDPClient();
client.connect("192.168.1.2", 25565);
client.disconnect();
```

#### Send message to server
```java
client.sendData("hello, buddy!");
```

#### Using timeouts
```java
// Packet timeout (milliseconds)
// Recommended value 100-200
client.setReceiveTimeout(200);

// Server disconnect timeout (milliseconds)
client.setDisconnectTimeout(5000);
```

#### Installing an event handler
The event handler class must implement the [UDPClientListener](/src/ru/lovenek0/network/PingPongUDP/UDPClientListener.java) interface
```java
Listener listener = new Listener();
client.setListener(listener);
```

## Additionally
Author: [LoveNek0](https://github.com/LoveNek0)