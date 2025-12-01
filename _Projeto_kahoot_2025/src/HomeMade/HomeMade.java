package HomeMade;

public abstract class HomeMade implements TimeoutWaiter{
    protected final long timeoutMil;
    protected int players;

    public HomeMade(long timeoutMil, int players) {
        this.timeoutMil = timeoutMil;
        this.players = players;
    }

    protected boolean timeoutOccurred = false;
    protected boolean finish = false;

    @Override
    public synchronized boolean waitForTimeout() {
        long endTime=System.currentTimeMillis() + timeoutMil;

        while(!finish){
            long now= System.currentTimeMillis();
            long toFinish = endTime - now;

            if(toFinish<=0){
                timeoutOccurred = true;
                finish = true;
                onFinish();
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
        return !timeoutOccurred;
    }

    @Override
    public boolean timeoutOccurred() {
        return timeoutOccurred;
    }

    protected void onFinish() {}
}
