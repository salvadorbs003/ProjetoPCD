package HomeMade;

public class IndiSem extends HomeMade {

    public IndiSem(long timeoutMil, int players) {
        super(timeoutMil, players);
        //TODO Auto-generated constructor stub
    }

    private int order=0; //arrival order counter

    public synchronized int points(int questPoint, boolean isCorrect){
        // If round is already closed/finished, no points
        if(finish) return 0;

        order++;    // Increment order (1st, 2nd, etc.)
        players--;  // Decrement waiting count

        // Calculate Score
        int score = 0;
        if (isCorrect) {
            // If 1st or 2nd to answer, double the points
            if (order <= 2) {
                score = questPoint * 2;
            } else {
                score = questPoint;
            }
        }

        // If everyone has answered, open the semaphore/latch
        if(players <= 0){
            finish = true;
            notifyAll();
        }
        
        return score;
    }
    
}
