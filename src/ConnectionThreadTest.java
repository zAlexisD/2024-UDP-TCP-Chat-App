import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;

public class ConnectionThreadTest {

    private static final int PORT = 12345;  // Port for testing
    private TestServer testServer;
    private Socket clientSocket;
    private ByteArrayOutputStream serverOutput;
    private final int timeSleep = 500;

    @BeforeEach
    public void setUp() throws IOException {
        // Start the test server on the defined port
        testServer = new TestServer(PORT);

        // Simulate a client socket that will connect to the test server
        clientSocket = new Socket("localhost", PORT);

        // Set up output stream to capture the server response
        serverOutput = new ByteArrayOutputStream();
    }

    @AfterEach
    public void tearDown() throws IOException {
        clientSocket.close();
        testServer.close();
    }

    @Test
    public void testExitConsoleCommand() throws IOException {
        // Accept the client connection in the server thread
        Socket serverClientSocket = testServer.acceptClient();

        // Create the input and output streams for the server thread
        InputStream clientInput = clientSocket.getInputStream();
        OutputStream clientOutput = clientSocket.getOutputStream();

        // Create a Runnable callback that prints the disconnect message
        Runnable disconnectCallback = () -> System.out.println("Client disconnected.");

        // Start the connection thread (this will handle communication)
        ConnectionThread connectionThread = new ConnectionThread(
                new TCPMultiServer(PORT), serverClientSocket, clientInput, clientOutput, disconnectCallback
        );
        connectionThread.start();

        // Send the "exit console" command to the server from the client
        clientOutput.write("exit console\n".getBytes());
        clientOutput.flush();

        // Allow some time for the server to process
        try {
            Thread.sleep(timeSleep);  // Sleep for 0.5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the server has sent the expected echo message back
        String response = serverOutput.toString();
        assertTrue(response.contains("Message received\n"), "Echo message was not sent.");

        // Verify that the client is properly disconnected after sending the exit command
        assertFalse(connectionThread.clientConnected, "Client should be disconnected after 'exit console' command.");
    }

    @Test
    public void testCloseServerCommand() throws IOException {
        // Accept the client connection in the server thread
        Socket serverClientSocket = testServer.acceptClient();

        // Create the input and output streams for the server thread
        InputStream clientInput = clientSocket.getInputStream();
        OutputStream clientOutput = clientSocket.getOutputStream();

        // Create a Runnable callback that prints the disconnect message
        Runnable disconnectCallback = () -> System.out.println("Client disconnected.");

        // Start the connection thread (this will handle communication)
        ConnectionThread connectionThread = new ConnectionThread(
                new TCPMultiServer(PORT), serverClientSocket, clientInput, clientOutput, disconnectCallback
        );
        connectionThread.start();

        // Send the "close server" command to the server from the client
        clientOutput.write("close server\n".getBytes());
        clientOutput.flush();

        // Allow some time for the server to process
        try {
            Thread.sleep(timeSleep);  // Sleep for 0.5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the server is properly closed
        assertFalse(connectionThread.clientConnected, "Client should be disconnected after 'close server' command.");
        assertFalse(connectionThread.getTcpMultiServer().serverConnected, "Server should be closed after 'close server' command.");
    }
}
