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

**Warning** : if running on IntelliJ, the command System.console() might return **null** because there is no default virtual console before the SDK version 22

See more on : https://bugs.openjdk.org/browse/JDK-8297226 or
https://youtrack.jetbrains.com/issue/IDEA-18814/IDEA-doesnt-work-with-System.console

## 1. Creating a UDP Client-Server
### 1.1 UDP Server
Launch server with : 
```bash
java UDPServer <port_number>
```
Replace **<port_number>** with the desired port on which launching the server. We usually use port 8080

Example : you can first test the server by connecting with **netcat**
```bash
netcat -u localhost <port_number>
```
Care : without option **-u**, the netcat connection use TCP by default
### 1.2 UDP Client
Launch the client with :
```bash
java UDPClient localhost <port_number>
```
## Creating a TCP Client-Server

## License 
This project is licensed under the [MIT License](LICENSE.md).

**This project is an academic exercise and was developed for educational purposes as part of the curriculum at ENSEA.**