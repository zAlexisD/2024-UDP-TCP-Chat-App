public class TCPExitCommandHandler implements CommandHandler{
    private final ConnectionThread connectionThread;

    public TCPExitCommandHandler(ConnectionThread connectionThread) {
        this.connectionThread = connectionThread;
    }

    @Override
    public void handle(String clientID,String receivedData){
        System.out.println("Client " + clientID + " left the chat.\n");
        connectionThread.clientConnected = false;
    }
}
