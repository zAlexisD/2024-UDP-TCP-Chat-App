import java.io.Console;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class UDPClient {
    private String serverHost;
    private int serverPort;

    //constructor
    public UDPClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    public void send() throws Exception {
        DatagramSocket socket = new DatagramSocket(); //open a socket 
        InetAddress serverInetAddress = InetAddress.getByName(serverHost); // get the ip address of a server knowing his name 
        Console console = System.console(); // get a console

        if (console == null) { 
            System.err.println("No console available.");
            System.exit(1);
        }

        while (true) {
            String userInput = console.readLine("Enter a message: "); // retrieve the user's message
            byte[] data = userInput.getBytes(StandardCharsets.UTF_8);
            DatagramPacket packet = new DatagramPacket(data, data.length, serverInetAddress, serverPort);
            socket.send(packet); // send datagram

            // Add condition to close the UDP server
            if (userInput.trim().equalsIgnoreCase("exit console")){
                System.out.println("Closing console...\n");
                break;
            }
        }
        socket.close();
    }
    public static void main(String[] args) throws Exception {
        if (args.length < 2) { // check the arguments
            System.err.println("Usage: java UDPClient <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        UDPClient client = new UDPClient(host, port);
        client.send();
    }
}




