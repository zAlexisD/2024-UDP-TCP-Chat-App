public class TCPCloseServerCommandHandler implements CommandHandler{
    private final ConnectionThread connectionThread;

    public TCPCloseServerCommandHandler(ConnectionThread connectionThread) {
        this.connectionThread = connectionThread;
    }

    @Override
    public void handle(String clientID, String receivedData){

    }
}
