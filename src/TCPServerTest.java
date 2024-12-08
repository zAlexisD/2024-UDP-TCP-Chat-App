import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class TCPServerTest {
    private Thread serverThread;
    private TCPServer server;

    private final int testPort = 12345;
    private final int timeSleep = 100;
    private final int maxBufSize = 1024;
    private final int dataOffset = 0;

    @BeforeEach
    void setUp() {
        // Start the TCP server in a separate thread
        server = new TCPServer(testPort);
        serverThread = new Thread(() -> {
            try {
                server.launch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Give the server some time to start
        try {
            Thread.sleep(timeSleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        // Stop the server thread
        serverThread.interrupt();
    }

    @Test
    void testServerStartsAndAcceptsConnection() {
        try (Socket clientSocket = new Socket("localhost", testPort)) {
            assertTrue(clientSocket.isConnected());
        } catch (Exception e) {
            fail("Client connection failed: " + e.getMessage());
        }
    }

    @Test
    void testServerEchoesMessage() {
        try (Socket clientSocket = new Socket("localhost", testPort)) {
            OutputStream outputStream = clientSocket.getOutputStream();
            byte[] message = "Hello, Server!".getBytes();
            outputStream.write(message);
            outputStream.flush();

            byte[] response = new byte[maxBufSize];
            int bytesRead = clientSocket.getInputStream().read(response);
            String echo = new String(response, dataOffset, bytesRead);

            assertEquals("Message received\n", echo.trim());
        } catch (Exception e) {
            fail("Echo test failed: " + e.getMessage());
        }
    }

    @Test
    void testServerHandlesExitConsoleCommand() {
        try (Socket clientSocket = new Socket("localhost", testPort)) {
            OutputStream outputStream = clientSocket.getOutputStream();
            byte[] message = "exit console".getBytes();
            outputStream.write(message);
            outputStream.flush();

            // Test that server still accepts new connections after "exit console"
            try (Socket anotherClient = new Socket("localhost", testPort)) {
                assertTrue(anotherClient.isConnected());
            }
        } catch (Exception e) {
            fail("Server failed to handle 'exit console' command: " + e.getMessage());
        }
    }
}
