package Protocolos;

import GameState.ScoreBoard;

public class GameFinished extends Mensagem {
    private static final long serialVersionUID = 1L;
    
    private final ScoreBoard finalScoreboard;

    public GameFinished(ScoreBoard finalScoreboard) {
        this.finalScoreboard = finalScoreboard;
    }

    public ScoreBoard getFinalScoreboard() {
        return finalScoreboard;
    }
}