import java.io.Console;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient {
    private String serverHost;
    private int serverPort;

    //constructor
    public UDPClient(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }

    // 
    public void send() throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress serverInetAddress = InetAddress.getByName(serverHost);
        Console console = System.console(); // Get a console 

        if (console == null) {
            System.out.println("Pas de console disponible.");
            return;
        }

        String userInput;
        userInput = console.readLine("Enter a message: ");
        while (userInput != null) {
            byte[] data = userInput.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(data, data.length, serverInetAddress, serverPort);
            socket.send(packet); // Envoyer le datagramme

            socket.close();
        }
    }
    }



