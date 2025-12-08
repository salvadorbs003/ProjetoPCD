package HomeMade;

public class GroupBarrier extends HomeMade {
    private int arrived = 0;

    // Constructor now only takes timeout and team size
    public GroupBarrier(long timeoutMil, int teamSize) {
        super(timeoutMil, teamSize);   // HomeMade handles timeout + finish
    }

    public synchronized void await() {
        if (finish) return;  // inherited from HomeMade

        arrived++;

        // Normal barrier completion: all team members answered
        if (arrived == players) {  // 'players' is inherited and equals teamSize
            finish = true;
            notifyAll(); // Release waiting threads
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
        // Triggered by HomeMade when timeout happens.
        // We don't need to do anything special here anymore.
        // HomeMade class will automatically call notifyAll() after this method returns.
    }
}