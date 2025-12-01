package GameState;

public class RoundLatch {
    
    private int remaining;
    private final long timeoutMil;

    private boolean timeoutOccurred = false;
    private boolean finish = false;

    public RoundLatch(int remaining, long timeoutMil) {
        this.remaining = remaining;
        this.timeoutMil = timeoutMil;
    }

    public synchronized void countAnswer(){
        if(finish) return;

        remaining--;
        if(remaining<=0){
            finish = true;
            notifyAll();
        }
    }

    public synchronized boolean awaitEnd(){
        long endTime=System.currentTimeMillis() + timeoutMil;

        while(!finish){
            long now= System.currentTimeMillis();
            long toFinish = endTime - now;

            if(toFinish<=0){
                timeoutOccurred = true;
                finish = true;
                notifyAll();
                break;
            }

            try {
                wait(toFinish);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return !timeoutOccurred; // true = all answered, false = timeout
    }

    public int getRemaining() {
        return remaining;
    }

    public long getTimeoutMil() {
        return timeoutMil;
    }

    public boolean timeoutOccurred() {
        return timeoutOccurred;
    }

    public boolean isFinish() {
        return finish;
    }
}
