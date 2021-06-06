# UDP Client-Server API
## Описание
API для приложений, использующих UDP соединение типа Пинг-Понг
## Установка
Добавить JAR файл в проект
## Примеры
### Примеры реализации:
#### Клиент
- [**Client.java**](/Examples/Client/Client.java)
- [**ClientListener.java**](/Examples/Client/ClientListener.java)
#### Сервер
- [**Server.java**](/Examples/Server/Server.java)
- [**ServerListener.java**](/Examples/Server/ServerListener.java)

### Сервер
#### Создание и запуск сервера
```java
// Первый вариант (адрес и порт по умолчанию: 127.0.0.1:20019)
UDPServer server = new UDPServer();
server.Run();

// Второй вариант (адрес и порт при создании объекта)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.Run();


// Третий вариант (адрес и порт заменяются перед запуском)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.Bing("192.168.1.2", 25565);
server.Run();
```
#### Использование таймаутов
```java
// Таймаут ожидания пакета (миллисекунды)
// Рекомендуется значение 100-200
server.SetReceiveTimeout(200);

// Таймаут отключения клиента (миллисекунды)
server.SetDisconnectTimeout(5000);
```

#### Установка обработчика событий
Класс обработчика событий должен реализовывать интерфейс [UDPServerListener](/src/ru/lovenek0/network/socket/UDPServerListener.java)
```java
Listener listener = new Listener();
server.SetListener(listener);
```

#### Действия с клиентами
```java
// Получить всех клиентов
ArrayList<UDPConnection> connections = server.GetConnections();

// Получить первого клиента из списка
UDPConnection connection = connections.get(0);

// Отправить клиенту сообщение
connection.SendData("Hello!");

// Отключить клиента
connection.Disconnect();
```

### Клиент
#### Создание клиента, подключение к серверу и отключение
```java
UDPClient client = new UDPClient();
client.Connect("192.168.1.2", 25565);
client.Disconnect();
```

#### Отправка сообщения серверу
```java
client.SendData("hello, buddy!");
```

#### Использование таймаутов
```java
// Таймаут ожидания пакета (миллисекунды)
// Рекомендуется значение 100-200
client.SetReceiveTimeout(200);

// Таймаут отключения от сервера (миллисекунды)
client.SetDisconnectTimeout(5000);
```

#### Установка обработчика событий
Класс обработчика событий должен реализовывать интерфейс [UDPClientListener](/src/ru/lovenek0/network/socket/UDPClientListener.java)
```java
Listener listener = new Listener();
client.SetListener(listener);
```

## Дополнительно
Автор: [LoveNek0](https://t.me/lovenek0)