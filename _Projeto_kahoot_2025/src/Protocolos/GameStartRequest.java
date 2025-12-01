package Protocolos;

import java.io.Serializable;

public class GameStartRequest extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;
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