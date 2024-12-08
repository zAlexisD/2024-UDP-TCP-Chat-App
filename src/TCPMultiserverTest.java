import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@code TCPMultiServer}.
 */
class TCPMultiServerTest {
    private TCPMultiServer serverThread;
    private Thread server;

    private final int testPort = 12345;
    private final int sleepTime = 70000;

    @BeforeEach
    public void setUp() throws IOException {
        // Start server on a test port (e.g., 12345)
        serverThread = new TCPMultiServer(testPort);
        server = new Thread(() -> {
            try {
                serverThread.launch();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        server.start();
    }

    @AfterEach
    public void tearDown() {
        // Stop server after each test
        serverThread.serverConnected = false;
    }

    @Test
    public void testServerIsRunning() {
        try (Socket socket = new Socket("localhost", testPort)) {
            assertTrue(socket.isConnected(), "Server should accept client connections.");
        } catch (IOException e) {
            fail("Server failed to start or accept connection: " + e.getMessage());
        }
    }

    @Test
    public void testServerTimeout() {
        // Test if server shuts down after timeout with no connections
        try {
            Thread.sleep(sleepTime); // wait for timeout (greater than the default timeout)
            assertEquals("Closed", serverThread.toString().split(":")[1].trim(), "Server should be closed after timeout.");
        } catch (InterruptedException e) {
            fail("Test was interrupted: " + e.getMessage());
        }
    }
}
