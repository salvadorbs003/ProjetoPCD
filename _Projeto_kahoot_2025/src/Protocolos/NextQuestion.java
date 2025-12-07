package Protocolos;

import java.util.List;
import GameState.ScoreBoard;
import Quizz.Pergunta;

public class NextQuestion extends Mensagem {
    private final String pinSala;
    private final int round;
    private final Pergunta pergunta;
    private final int time;
    private final ScoreBoard scoreboard;

    public NextQuestion(String pinSala, int round, int time,  Pergunta pergunta, ScoreBoard scoreboard) {
        this.pinSala = pinSala;
        this.round = round;
        this.pergunta = pergunta;
        this.time = time;
        this.scoreboard = scoreboard;
    }

    public String getPinSala() {
        return pinSala;
    }

    public int getRound() {
        return round;
    }

    public Pergunta getPergunta() {
        return pergunta;
    }

    public int getTime() {
        return time;
    }

    public ScoreBoard getScoreboard() {
        return scoreboard;
    }
}
