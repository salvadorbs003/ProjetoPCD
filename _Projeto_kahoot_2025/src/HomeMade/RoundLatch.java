package HomeMade;

public class RoundLatch extends HomeMade{
    
    public RoundLatch(long timeoutMil, int players) {
        super(timeoutMil, players);
        //TODO Auto-generated constructor stub
    }

    public synchronized void countAnswer(){
        if(finish) return;

        players--;
        if(players<=0){
            finish = true;
            notifyAll();
        }
    }

}
