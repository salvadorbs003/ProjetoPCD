package HomeMade;

public class IndiSem extends HomeMade {

    public IndiSem(long timeoutMil, int players) {
        super(timeoutMil, players);
        //TODO Auto-generated constructor stub
    }

    private int order=0; //arrival order counter

    public synchronized int points(int questPoint){
        if(!finish) return 0;

        order++;
        players--;  //smpr menos qnd uma pessoa responde até chegar ao 0

        int score=0;
        if(order <= 2) score = questPoint*2;
        if(players == 0){
            finish = true;
            notifyAll();
        }
        return score;
    }
    
}
