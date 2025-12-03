package GameState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Quizz.Pergunta;

/**
 * Mantém o estado do jogo no cliente sem recorrer a singletons estáticos.
 */
public class ClientGameState {
    private final List<Pergunta> perguntas;
    private final List<Jogador> jogadores = new ArrayList<>();
    private Jogador jogadorAtual;

    public ClientGameState(List<Pergunta> perguntas) {
        this.perguntas = perguntas == null ? new ArrayList<>() : new ArrayList<>(perguntas);
    }

    public List<Pergunta> getPerguntas() {
        return Collections.unmodifiableList(perguntas);
    }

    public Pergunta getPerguntaPorIndice(int indice) {
        if (indice < 0 || indice >= perguntas.size()) {
            return null;
        }
        return perguntas.get(indice);
    }

    public void adicionarJogador(Jogador jogador) {
        if (jogador != null && !jogadores.contains(jogador)) {
            jogadores.add(jogador);
        }
    }

    public List<Jogador> getJogadores() {
        return Collections.unmodifiableList(jogadores);
    }

    public void definirJogadorAtual(Jogador jogador) {
        this.jogadorAtual = jogador;
        adicionarJogador(jogador);
    }

    public Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    public int getIndiceJogador(Jogador jogador) {
        return jogadores.indexOf(jogador);
    }

    public void adicionarPontos(Jogador jogador, int pontos) {
        if (jogador == null) return;
        int idx = jogadores.indexOf(jogador);
        if (idx >= 0) {
            jogadores.get(idx).aumentarPontuacao(pontos);
        }
    }

    public String[] getNomes() {
        return jogadores.stream().map(Jogador::getNome).toArray(String[]::new);
    }

    public int[] getPontuacoes() {
        return jogadores.stream().mapToInt(Jogador::getPontuacao).toArray();
    }
}
