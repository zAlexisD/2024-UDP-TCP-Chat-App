import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UDPClientTest {

    private static final int TEST_PORT = 9876;
    private final int maxBufSize = 1024;
    private UDPClient udpClient;
    private DatagramSocket serverSocket;

    @BeforeEach
    void setUp() throws Exception {
        udpClient = new UDPClient("localhost", TEST_PORT);

        // Create a mock server to receive client messages
        serverSocket = new DatagramSocket(TEST_PORT);
        new Thread(() -> {
            try {
                byte[] buffer = new byte[maxBufSize];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                while (!serverSocket.isClosed()) {
                    serverSocket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Server received: " + received);
                }
            } catch (Exception e) {
                // Socket closed
            }
        }).start();
    }

    @Test
    void testSendMessage() throws Exception {
        // Simulate console input
        String input = "Hello, server!\nexit console\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // Capture console output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // Send messages
        udpClient.send();

        // Check server-side logs for message receipt
        assertTrue(outContent.toString().contains("Hello, server!"), "Client should send the message");
    }

    @Test
    void testHelpCommand() throws Exception {
        String input = "?\nexit console\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        udpClient.send();

        assertTrue(outContent.toString().contains("CTRL + D"), "Help command should display usage info");
    }
}
