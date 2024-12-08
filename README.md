# UDP/TCP Chat App with Java sockets
3rd year RTS project - Java for networks
## Objectives
Develop a Client-Server Chat application, in UDP and TCP
## Getting started
### Prerequisites
- The programs are written in JAVA, using the Oracle OpenJDK version 21
### Compile JAVA code
```bash
javac <javaclass.java>
```
### Execute JAVA compiled code
```bash
java <javaclass.class> <arguments>
```
You can also directly execute the code in an IDE.

**Warning** : if running on IntelliJ, the command `System.console()` might return `null` because there is no default virtual console integrated before the SDK version 22

See more on : https://bugs.openjdk.org/browse/JDK-8297226 or
https://youtrack.jetbrains.com/issue/IDEA-18814/IDEA-doesnt-work-with-System.console

## 1. Creating a UDP Client-Server
**Features :**
- Server : 
  - Wait for a client's datagram packet in UDP
  - Display client's address and port number
  - Display client's messages
  - Act accordingly to client's commands
- Client : 
  - Send packet to a UDP server
  - Send any message
  - Execute commands :
    - `?` : Display available commands
    - CTRL+D or `exit console` : close the client session
    - `close server` : close the server instance

### 1.1 UDP Server
Launch server with : 
```bash
java UDPServer <server_port_number>
```
Replace **<server_port_number>** with the desired port on which launching the server. We usually use port 8080.

Example : you can first test the server by connecting with **netcat**
```bash
netcat -u localhost <server_port_number>
```
Care : without option **-u**, the netcat connection use TCP by default
### 1.2 UDP Client
Launch the client with :
```bash
java UDPClient localhost <server_port_number>
```
## 2. Creating a TCP Client-Server
**Features :**
- Server :
  - Wait for a client's connection in TCP
  - Has a 1 min timeout 
  - Display the remaining time every 10 seconds
  - Reset the countdown after each client's session closure
  - Display client's address and port number
  - Display client's messages
  - Act accordingly to client's commands
  - Send echo feed-back when message received
- Client :
  - Connect to a TCP server
  - Send any message
  - Receive echo from the server
  - Execute commands :
    - `?` : Display available commands
    - CTRL+D or `exit console` : close the client session
    - `close server` : close the server instance

**WIP :**
- Reset time correctly if the client's connection loss (ex : CTRL + C)
### 2.1 TCP Server
Launch server with :
```bash
java TCPServer <server_port_number>
```
Replace **<server_port_number>** with the desired port on which launching the server. We usually use port 8080.
### 2.2 TCP Client
Launch the client with :
```bash
java TCPClient localhost <server_port_number>
```
### 2.3 Server accepting multiple TCP connections
**Features :**
- Same features as the TCP server, but it can accept multiple connections
- Multiple connections managed by ConnectionThread class.
- Reset the countdown when there is no active connection
- Count the number of active connections

```bash
java TCPmultiServer <server_port_number>
```
Replace **<server_port_number>** with the desired port on which launching the server. We usually use port 8080.

ThreadTest main method : count the number of calls for the 2 threads.

## Unit Testing
**WIP :**
- Unit testing on Clients does not work due to IntelliJ virtual console issue. (Might need to change OpenJDK version)
- UDPServer : issue with server state
- TCPServer : issue with address in use
- ConnectionThread : socket issues in the test class

## Overall WIP
- Manage commands with polymorphism to avoid the multiple if statements (Command Handler classes)
- Add more commands
- When a client uses a command, don't display it or manage it differently on the servers
- Improve Unit testing
- Code coverage
- JAR archives
- Use of packages
- change some protected values and make getter functions.
## License 
This project is licensed under the [MIT License](LICENSE.md).

**This project is an academic exercise and was developed for educational purposes as part of the curriculum at ENSEA.**