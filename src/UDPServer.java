import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPServer {
    private int listeningPort;
    private String serverState;
    private final int defaultPort = 0;
    private final int maxBufSize = 1024;

    public UDPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    public UDPServer() {
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void launch() throws IOException {
        DatagramSocket socket = null;

        try {
            this.serverState = "Running";
            socket = new DatagramSocket(this.listeningPort);
            System.out.println("UDPServer is running and listening on port " + this.listeningPort);

            byte[] buf = new byte[maxBufSize];

            while(true){
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                String receivedData = new String(packet.getData(),0, packet.getLength(), StandardCharsets.UTF_8);

                System.out.println("User in " + clientAddress + " says on port " + clientPort + ": " + receivedData + "\n");

                // Add condition to tell when the user disconnects
                if(receivedData.trim().equalsIgnoreCase(("exit console"))){
                    System.out.println("User at "+ clientAddress + " left the chat");
                }
                // Add condition to close the UDP server
                if (receivedData.trim().equalsIgnoreCase("close server")){
                    System.out.println("Server closing...\n");
                    break;
                }
            }
        } finally {
            this.serverState = "Closed";
            System.out.println("Server closed\n");
            if(socket != null && !socket.isClosed()){
                socket.close();
            }
        }
    }

    @Override
    public String toString() {
        return "UDP server status on port " + this.listeningPort + ": " + this.serverState;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1){
            System.err.println("Usage: java UDPServer <listening port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);

        UDPServer servUDP = new UDPServer(port);
        servUDP.launch();
        System.out.println(servUDP);
    }

}
