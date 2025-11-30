package Protocolos;

import java.io.Serializable;

public final class TeamStatusResponse extends Mensagem{
    private static final long serialVersionUID = 1L;
    public enum Status {
        COMPLETA, INCOMPLETA
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

    public static int getMaxPlayers() {
        return MAX_PLAYERS;
    }

    public static TeamStatusResponse completa(String equipaNome, int playercount) {
        return new TeamStatusResponse(equipaNome, Status.COMPLETA, playercount);
    }
    public static TeamStatusResponse incompleta(String equipaNome, int playercount) {
        return new TeamStatusResponse(equipaNome, Status.INCOMPLETA, playercount);
    }

    public boolean isCompleta() {
        return status == Status.COMPLETA;
    }

}