package Protocolos;

public final class JoinRequest extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;
    private final String jogadorNome;
    private final String nomeEquipa;

    public JoinRequest(String pinSala, String jogadorNome, String nomeEquipa) {
        this.pinSala = pinSala;
        this.jogadorNome = jogadorNome;
        this.nomeEquipa = nomeEquipa;
    }

    public String getPinSala() {
        return pinSala;
    }

    public String getJogadorNome() {
        return jogadorNome;
    }

    public String getNomeEquipa() {
        return nomeEquipa;
    }
}
