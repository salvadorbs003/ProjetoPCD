package GameState;

import java.util.ArrayList;
import java.util.List;

public class Lista_Jogadores {

	private static List<Jogador> jogadores = new ArrayList<>();
	
	private static Jogador jogadorAtual;

    public static void adicionarJogador(Jogador jogador) {
        jogadores.add(jogador);
    }

    public static List<Jogador> getJogadores() {
        return jogadores;
    }

    public static void limparJogadores() {
        jogadores.clear();
    }

    // vai buscar o jogador pelo id
    public static Jogador getJogadorPorId(int id) {
        if (id >= 0 && id < jogadores.size()) {
            return jogadores.get(id);
        }
        return null;
    }

    public static void adicionarPontos(int idJogador, int pontos) {
        Jogador j = getJogadorPorId(idJogador);
        if (j != null) {
            j.aumentarPontuacao(pontos);
        }
    }

    public static String[] getNomes() {
        return jogadores.stream().map(Jogador::getNome).toArray(String[]::new);
    }

    public static int[] getPontuacoes() {
        return jogadores.stream().mapToInt(Jogador::getPontuacao).toArray();
    }
    
    public static void definirJogadorAtual(Jogador j) {
        jogadorAtual = j;
    }

    public static Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    public static int getIdJogadorAtual() {
        if (jogadorAtual == null) return -1;
        return jogadores.indexOf(jogadorAtual);
    }
    
}
