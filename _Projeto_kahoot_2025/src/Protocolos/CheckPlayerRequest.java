package Protocolos;

import java.io.Serializable;

public class CheckPlayerRequest extends Mensagem{
    private static final long serialVersionUID = 1L;
    private final String namePlayer;
    private final String pin;
    
    public CheckPlayerRequest(String namePlayer, String pin) {
        this.namePlayer = namePlayer;
        this.pin = pin;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public String getPin() {
        return pin;
    }

    
}