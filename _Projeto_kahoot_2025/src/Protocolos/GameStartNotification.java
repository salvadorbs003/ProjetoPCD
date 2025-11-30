package Protocolos;

import java.io.Serializable;

public class GameStartNotification extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;

    public GameStartNotification(String pinSala) {
        this.pinSala = pinSala;
    }

    public String getPinSala() {
        return pinSala;
    }
}