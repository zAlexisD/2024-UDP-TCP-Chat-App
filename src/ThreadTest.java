public class ThreadTest extends java.lang.Thread{
    public int counter = 0;

    @Override 
    public void run( ) {
        while (true) {
            System.out.println (getName( ) + ":" + counter++);
            try { 
                sleep(100); // 100 ms
            } 
            catch(InterruptedException e) {
                System.out.println ("Thread interrupted"+e.getMessage());
            }
        }
    }

    public static void main(String[] args){
        ThreadTest t1 = new ThreadTest();
        ThreadTest t2 = new ThreadTest();

        t1.start();
        t2.start();
    }
}
