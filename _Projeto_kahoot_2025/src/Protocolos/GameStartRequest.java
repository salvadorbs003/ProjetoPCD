package Protocolos;

public class GameStartRequest extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;
    private final int qnum;
    private final int timeLimitSeconds;
  
    
    public GameStartRequest(String pinSala,int timeLimitSeconds, int qnum) {
            this.pinSala = pinSala;
            this.timeLimitSeconds = timeLimitSeconds;
            this.qnum = qnum;
        }
        
        public String getPinSala() {
            return pinSala;
        }
        public int getTimeLimitSeconds() {
            return timeLimitSeconds;
        }
        
        public int getQuestion() {
            return qnum;
        }
    }
