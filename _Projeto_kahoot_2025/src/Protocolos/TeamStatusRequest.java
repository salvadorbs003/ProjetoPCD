package Protocolos;

import java.io.Serializable;

public final class TeamStatusRequest implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String pinSala;
    private final String equipaNome;
    private final String jogadorNome;

    
    public TeamStatusRequest(String pinSala, String equipaNome, String jogadorNome) {
        this.pinSala = pinSala;
        this.equipaNome = equipaNome;
        this.jogadorNome = jogadorNome;
    }

    public String getJogadorNome() {
        return jogadorNome;
    }

    public String getPinSala() {
        return pinSala;
    }

    public String getEquipaNome() {
        return equipaNome;
    }
}