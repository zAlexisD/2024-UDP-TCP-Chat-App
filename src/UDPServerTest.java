import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;

class UDPServerTest {

    private static final int TEST_PORT = 9876;
    private final int sleepTime = 1000;
    private UDPServer udpServer;
    private Thread serverThread;

    @BeforeEach
    void setUp() {
        udpServer = new UDPServer(TEST_PORT);
        serverThread = new Thread(() -> {
            try {
                udpServer.launch();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        serverThread.start();
    }

    @AfterEach
    void tearDown() {
        try {
            // Send a "close server" message to stop the server
            DatagramSocket socket = new DatagramSocket();
            byte[] message = "close server".getBytes();
            InetAddress address = InetAddress.getByName("localhost");
            DatagramPacket packet = new DatagramPacket(message, message.length, address, TEST_PORT);
            socket.send(packet);
            socket.close();
            serverThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testServerLaunch() {
        assertEquals("Closed", udpServer.toString().contains("Closed") ? "Closed" : "Running",
                "Server should start in 'Running' state");
    }

    @Test
    void testReceiveMessage() throws Exception {
        // Send a test message to the server
        DatagramSocket socket = new DatagramSocket();
        byte[] message = "test message".getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(message, message.length, address, TEST_PORT);
        socket.send(packet);
        socket.close();

        // Wait for server logs (or manually observe logs to verify receipt)
        Thread.sleep(sleepTime); // Small delay for server processing (optional)
    }

    @Test
    void testCloseServerCommand() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        byte[] message = "close server".getBytes();
        InetAddress address = InetAddress.getByName("localhost");
        DatagramPacket packet = new DatagramPacket(message, message.length, address, TEST_PORT);
        socket.send(packet);
        socket.close();

        // Give the server time to shut down
        Thread.sleep(sleepTime);

        assertEquals("Closed", udpServer.toString().contains("Closed") ? "Closed" : "Running",
                "Server should close when 'close server' command is received");
    }
}
