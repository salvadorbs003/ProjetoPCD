package Protocolos;

public class NextQuestionRequest extends Mensagem {
    private static final long serialVersionUID = 1L;
    
    private final String pinSala;
    private final int currentRound; // The round the client just finished

    public NextQuestionRequest(String pinSala, int currentRound) {
        this.pinSala = pinSala;
        this.currentRound = currentRound;
    }

    public String getPinSala() { return pinSala; }
    public int getCurrentRound() { return currentRound; }
}
