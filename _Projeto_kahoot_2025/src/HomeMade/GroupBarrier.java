package HomeMade;

public class GroupBarrier{
    private int parties;    // team size
    private int arrived = 0;

    private boolean finished = false;
    private boolean timeout = false;
    private Runnable barrierAction;

    public GroupBarrier(int parties,Runnable barrierAction ) {
        this.parties = parties;
        this.barrierAction = barrierAction;
    }

    public synchronized void await() {
        if (finished) return;

        arrived++;

        if (arrived >= parties) {
            // everyone in team has answered
            finished = true;
            if (barrierAction != null) barrierAction.run();
            notifyAll();
            return;
        }

        while (!finished) {
            try { wait(); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
    }

    public synchronized void timeoutRelease() {
        if (finished) return;
        timeout = true;
        finished = true;
        if (barrierAction != null) barrierAction.run();
        notifyAll();
    }

    public boolean timeoutOccurred() { return timeout; }


}
