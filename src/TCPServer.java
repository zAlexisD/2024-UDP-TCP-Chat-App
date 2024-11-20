import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetAddress;


public class TCPServer {
    private int listeningPort;
    private String serverState;

    public TCPServer(int listeningPort) {
        this.listeningPort = listeningPort;
        this.serverState = "Closed";
    }

    public TCPServer() {
        int defaultPort = 0;
        this.listeningPort = defaultPort;
        this.serverState = "Closed";
    }

    public int getListeningPort() {
        return listeningPort;
    }

    public void launch() throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(this.listeningPort);
            this.serverState = "Running";
            System.out.println("Server is running and listening on port " + this.listeningPort);

            while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("COnnection from client :" + clientSocket.getInetAddress());

            InputStream input = clientSocket.getInputStream();                // InputStream to read the server response
            OutputStream output = clientSocket.getOutputStream();


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
