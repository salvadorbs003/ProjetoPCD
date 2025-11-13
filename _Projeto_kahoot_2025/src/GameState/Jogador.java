package GameState;

public class Jogador {
    private String nome;
    private int pontuacao;
    private String equipa; // Nome da equipa

    public Jogador(String nome, String equipa) {
        this.nome = nome;
        this.pontuacao = 0;
        this.equipa = equipa;
    }

    public String getNome() {
        return nome;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void aumentarPontuacao(int pontos) {
        this.pontuacao += pontos;
    }

    public String getEquipa() {
        return equipa;
    }

    public void setEquipa(String equipa) {
        this.equipa = equipa;
    }

    @Override
    public String toString() {
        return nome + " (" + pontuacao + " pts) - " + equipa;
    }
}