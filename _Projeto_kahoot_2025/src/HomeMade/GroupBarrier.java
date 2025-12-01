package HomeMade;

public class GroupBarrier extends HomeMade{
    private int arrived = 0;
    private Runnable barrierAction;

    public GroupBarrier(long timeoutMil, int teamSize, Runnable barrierAction) {
        super(timeoutMil, teamSize);   // HomeMade handles timeout + finish
        this.barrierAction = barrierAction;
    }

    public synchronized void await() {
        if (finish) return;  // inherited from HomeMade

        arrived++;

        // Normal barrier completion: all team members answered
        if (arrived == players) {  // 'players' is inherited and equals teamSize
            finish = true;
            if (barrierAction != null) barrierAction.run();
            notifyAll();
            return;
        }

        // Otherwise wait for normal completion OR timeout
        while (!finish) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    protected synchronized void onFinish() {
        // Triggered by HomeMade when timeout happens
        if (barrierAction != null)
            barrierAction.run();
        // notifyAll() is already done in HomeMade after calling onFinish()
    }


}
