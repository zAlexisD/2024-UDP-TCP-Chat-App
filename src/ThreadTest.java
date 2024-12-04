/**
 * Demonstrates the creation and execution of threads in Java using the {@link java.lang.Thread} class.
 * The program creates two threads that run concurrently, incrementing and displaying a counter with a slight delay.
 */
public class ThreadTest extends java.lang.Thread{
    public int counter = 0;

    /**
     * This method is called when the thread is started.
     * It runs an infinite loop, printing the thread's name and the current counter value,
     * and then increments the counter with a delay of 100 milliseconds.
     */
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

    /**
     * The main method demonstrates the creation and starting of multiple threads.
     * Two threads are created, each running its own counter concurrently.
     *
     * @param args command-line arguments (not used in this program)
     */
    public static void main(String[] args){
        ThreadTest t1 = new ThreadTest();
        ThreadTest t2 = new ThreadTest();

        t1.start();
        t2.start();
    }
}
