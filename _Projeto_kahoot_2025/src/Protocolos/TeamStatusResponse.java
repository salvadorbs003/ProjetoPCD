package Protocolos;

import java.util.List;

import GameState.Jogador;

public class TeamStatusResponse {
    public enum Status{
        OK, ERROR
    }

    private final String equipaNome;
    private final Status status; 
    private final int playercount;
    private static final int MAX_PLAYERS = 2;

    public TeamStatusResponse(String equipaNome, Status status, int playercount) {
        this.equipaNome = equipaNome;
        this.status = status;
        this.playercount = playercount;
    }

    public String getEquipaNome() {
        return equipaNome;
    }
    public Status getStatus() {
        return status;
    }

    public int getPlayerCount() {
        return playercount;
    }
}
