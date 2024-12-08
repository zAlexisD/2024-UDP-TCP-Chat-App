import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class TCPClientTest {
    private Thread serverThread;
    private TCPServer server;

    private final int testPort = 12345;
    private final int sleepTime = 100;

    @BeforeEach
    void setUp() {
        // Start the TCP server
        server = new TCPServer(testPort);
        serverThread = new Thread(() -> {
            try {
                server.launch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        serverThread.start();

        // Wait for the server to start
        try {
            Thread.sleep(sleepTime);
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
    void testClientSendsMessage() {
        try {
            TCPClient client = new TCPClient("localhost", testPort);

            // Simulate console input
            String simulatedInput = "Hello, Server!\nexit console\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Capture console output
            ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(consoleOutput));

            client.send();

            String output = consoleOutput.toString();
            assertTrue(output.contains("Server echo: Message received"));
        } catch (Exception e) {
            fail("Client message test failed: " + e.getMessage());
        }
    }

    @Test
    void testClientHandlesExitConsoleCommand() {
        try {
            TCPClient client = new TCPClient("localhost", testPort);

            // Simulate console input
            String simulatedInput = "exit console\n";
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

            // Capture console output
            ByteArrayOutputStream consoleOutput = new ByteArrayOutputStream();
            System.setOut(new PrintStream(consoleOutput));

            client.send();

            String output = consoleOutput.toString();
            assertTrue(output.contains("Closing console..."));
        } catch (Exception e) {
            fail("Client exit console test failed: " + e.getMessage());
        }
    }
}
