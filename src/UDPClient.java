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


    public void send() throws Exception {
        DatagramSocket socket = new DatagramSocket(); //open a socket 
        InetAddress serverInetAddress = InetAddress.getByName(serverHost); // get the ip address of a server knowing his name 
        Console console = System.console(); // get a console 

        if (console == null) { 
            System.out.println("No console available."); 
            return;
        }

        String userInput;
        userInput = console.readLine("Enter a message: "); // retrieve the user's message
        while (userInput != null) {
            byte[] data = userInput.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(data, data.length, serverInetAddress, serverPort);
            socket.send(packet); // send the datagramme

            socket.close();
        }
    }
    }



