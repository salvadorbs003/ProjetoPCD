package Protocolos;

import java.io.Serializable;
import java.util.List;

import Protocolos.NextQuestion.PlayerScore;

public class GameStartRequest extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;
    private final int round = 1;
    private final int totalQuestions;
    private final String questionText;
    private final List<String> options;
    private final int timeLimitSeconds;
    private final List<PlayerScore> scoreboard;
    private final String namePlayer;
    private final String nameTeam;

    
    public GameStartRequest(String pinSala, String namePlayer, String nameTeam) {
        this.pinSala = pinSala;
        this.namePlayer = namePlayer;
        this.nameTeam = nameTeam;
    }
    
    public String getPinSala() {
        return pinSala;
    }
    
    public String getNamePlayer() {
        return namePlayer;
    }

    public String getNameTeam() {
        return nameTeam;
    }
}