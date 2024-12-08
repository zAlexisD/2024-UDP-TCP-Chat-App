public class UDPExitCommandHandler implements CommandHandler{

    @Override
    public void handle(String clientID,String receivedData){
        receivedData = "";
        System.out.println("Bye Bye user "+ clientID + " ヾ(・ω・*)");
    }
}
