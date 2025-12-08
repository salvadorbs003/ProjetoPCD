package Protocolos;

public class SubmitAnswerRequest extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;
    private final String nomeJogador;
    private final String nomeEquipa;
    private final int answerIndex; // The index of the option chosen (0-3)

    public SubmitAnswerRequest(String pinSala, String nomeJogador, String nomeEquipa, int answerIndex) {
        this.pinSala = pinSala;
        this.nomeJogador = nomeJogador;
        this.nomeEquipa = nomeEquipa;
        this.answerIndex = answerIndex;
    }

    public String getPinSala() { return pinSala; }
    public String getNomeJogador() { return nomeJogador; }
    public String getNomeEquipa() { return nomeEquipa; }
    public int getAnswerIndex() { return answerIndex; }
}