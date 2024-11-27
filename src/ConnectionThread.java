import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;

public class ConnectionThread extends Thread {
    private final Socket clientSocket;
    private final int maxBufSize = 1024;
    
    private final long timeout; 
    private final int interval; 
    private final long startTime; 

    public ConnectionThread(Socket clientSocket, long timeout, int interval) {
        this.clientSocket = clientSocket;
        this.timeout = timeout;
        this.interval = interval;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        System.out.println("Handling client: " + clientSocket.getInetAddress());
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {

            byte[] buffer = new byte[maxBufSize];
            String echo = "Message received";
            byte[] echoBuffer = echo.getBytes(StandardCharsets.UTF_8);

            while (true) {
                // Check for timeout
                if (System.currentTimeMillis() - startTime > timeout) {
                    System.out.println("Timeout reached for client: " + clientSocket.getInetAddress());
                    break;
                }

                // Read message from client
                int bytesRead = input.read(buffer);
                if (bytesRead == -1) {
                    System.out.println("Client disconnected: " + clientSocket.getInetAddress());
                    break;
                }

                String receivedData = new String(buffer, 0, bytesRead, StandardCharsets.UTF_8);
                System.out.println("Client " + clientSocket.getInetAddress() + " says: " + receivedData);

                // Handle commands
                if (receivedData.trim().equalsIgnoreCase("exit console")) {
                    System.out.println("Client " + clientSocket.getInetAddress() + " left the chat.");
                    break;
                }

                if (receivedData.trim().equalsIgnoreCase("close server")) {
                    System.out.println("Client " + clientSocket.getInetAddress() + " requested server shutdown.");
                    System.exit(0); // Shut down the server
                }

                // Send echo response
                output.write(echoBuffer);
                output.flush();

                // Wait for the interval before the next read
                sleep(interval);
            }
        } catch (Exception e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
            System.out.println("Connection closed for client: " + clientSocket.getInetAddress());
        }
    }
}
