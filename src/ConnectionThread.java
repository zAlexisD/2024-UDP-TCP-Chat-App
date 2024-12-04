import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static java.lang.Thread.sleep;

public class ConnectionThread extends Thread {
    private final Socket clientSocket;
    private final InputStream clientInput;
    private final OutputStream clientOutput;
    private final int maxBufSize = 1024;

    // Set echo message
    String echo = "Message received\n";
    byte[] echo_buf = echo.getBytes(StandardCharsets.UTF_8);

    public ConnectionThread(Socket clientSocket,InputStream clientInput,OutputStream clientOutput) {
        this.clientSocket = clientSocket;
        this.clientInput = clientInput;
        this.clientOutput = clientOutput;
    }

    // WIP : for now returns err : Socket closed
    @Override
    public void run() {
        try {
            while (true) {
                // WIP : manage ? and not displaying when client use a command
                // Get the Client's message
                byte[] buf = new byte[maxBufSize];
                int bytesRead = clientInput.read(buf);
                String receivedData = new String(buf, StandardCharsets.UTF_8);
                System.out.println("Client " + clientSocket.getInetAddress() + " says : " + receivedData);

                // WIP : manage when client connection is lost
                // Manage user's disconnection
                if (receivedData.trim().equalsIgnoreCase("exit console")) {
                    System.out.println("Client " + clientSocket.getInetAddress() + " left the chat.\n");
                    break;
                }

                // Manage TCP server closure commanded by client
                if (receivedData.trim().equalsIgnoreCase("close server")) {
                    System.out.println("Client " + clientSocket.getInetAddress() + " requested server shutdown.");
                    System.out.println("Server closing...\n");
                    break;
                }

                // Send an echo to the client
                clientOutput.write(echo_buf);
                clientOutput.flush();
                // WIP: handle when no response
            }
        } catch (IOException e){
            System.err.println("Error in the client connection: "+e.getMessage());
        } finally {
            // Resources closure
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}
