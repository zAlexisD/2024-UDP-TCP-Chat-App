// Test class to simulate the server behavior for the ConnectionThreadTest class

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TestServer {
    private ServerSocket serverSocket;

    public TestServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }

    public Socket acceptClient() throws IOException {
        return serverSocket.accept();
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
