# UDP Client-Server API
## Описание
API для приложений, использующих мультиклиентное UDP соединение типа Пинг-Понг
## Установка
Добавить JAR файл в проект

### Сервер
#### Создание и запуск сервера
```java
// Первый вариант (адрес и порт по умолчанию: 127.0.0.1:20019)
UDPServer server = new UDPServer();
server.start();

// Второй вариант (адрес и порт при создании объекта)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.start();

// Третий вариант (при создании задаются адрес, порт, размер пакетов, 
//                  таймаут ожидания клиента и таймаут получения пакета)
UDPServer server = new UDPServer("127.0.0.1", 23344, 2048, 10000, 500);
server.start();
        
        
// Четвертый вариант (адрес и порт заменяются перед запуском)
UDPServer server = new UDPServer("192.168.1.2", 25565);
server.bind("192.168.1.2", 25565);
server.start();
```
#### Использование таймаутов
```java
// Таймаут ожидания пакета (миллисекунды)
// Рекомендуется значение 100-200
server.setReceiveTimeout(200);

// Таймаут отключения клиента (миллисекунды)
server.setDisconnectTimeout(5000);
```

#### Установка обработчика событий
Класс обработчика событий должен реализовывать интерфейс [UDPServerListener](/src/ru/lovenek0/network/PingPongUDP/UDPServerListener.java)
```java
Listener listener = new Listener();
server.setListener(listener);
```

#### Действия с клиентами
```java
// Получить всех клиентов
UDPConnection[] connections = server.getConnections();

// Получить первого клиента из списка
UDPConnection connection = connections[0];

// Отправить клиенту сообщение
connection.sendData("Hello!");

// Отключить клиента
connection.disconnect();
```

### Клиент
#### Создание клиента, подключение к серверу и отключение
```java
UDPClient client = new UDPClient();
client.connect("192.168.1.2", 25565);
client.disconnect();
```

#### Отправка сообщения серверу
```java
client.sendData("hello, buddy!");
```

#### Использование таймаутов
```java
// Таймаут ожидания пакета (миллисекунды)
// Рекомендуется значение 100-200
client.setReceiveTimeout(200);

// Таймаут отключения от сервера (миллисекунды)
client.setDisconnectTimeout(5000);
```

#### Установка обработчика событий
Класс обработчика событий должен реализовывать интерфейс [UDPClientListener](/src/ru/lovenek0/network/PingPongUDP/UDPClientListener.java)
```java
Listener listener = new Listener();
client.setListener(listener);
```

## Дополнительно
Автор: [LoveNek0](https://github.com/LoveNek0)