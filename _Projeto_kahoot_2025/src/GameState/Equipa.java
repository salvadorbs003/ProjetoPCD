package GameState;

import java.util.ArrayList;
import java.util.List;

public class Equipa {

    //private static List<Equipa> todasEquipas = new ArrayList<>();

    private String nome;
    private List<Jogador> jogadores;

    public Equipa(String nome) {
        this.nome = nome;
        this.jogadores = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public List<Jogador> getJogadores() {
        return jogadores;
    }

    public boolean adicionarJogador(Jogador jogador) {
        if (!estaCompleta()) {
            jogadores.add(jogador);
            jogador.setEquipa(this.nome); 
            return true;
        }
        return false; 
    }

    public boolean estaCompleta() {
        return jogadores.size() == 2;
    }

    public int getNumeroJogadores() {
        return jogadores.size();
    }

    public String getStatusEquipa() {
        return this.nome + " - " + this.jogadores.size() + "/2";
    }
    
    //Não faz sentido o static!!!
    // public static boolean adicionarJogadorAEquipa(Jogador jogador, String nomeEquipa) {
    //     Equipa equipa = buscarEquipa(nomeEquipa);
    //     if (equipa == null) {
    //         equipa = new Equipa(nomeEquipa);
    //         todasEquipas.add(equipa);
    //     }
    //     return equipa.adicionarJogador(jogador);
    // }

    // public static Equipa buscarEquipa(String nomeEquipa) {
    //     for (Equipa e : todasEquipas) {
    //         if (e.getNome().equalsIgnoreCase(nomeEquipa)) {
    //             return e;
    //         }
    //     }
    //     return null;
    // }

    // public static boolean podeIniciarJogo() {
    //     int equipasCompletas = 0;
        
    //     for (Equipa e : todasEquipas) {
    //         if (e.estaCompleta()) {
    //             equipasCompletas++;
    //         }
    //     }
    //     return equipasCompletas >= 2;
    // }
   
    // public static List<Equipa> getTodasEquipas() {
    //     return new ArrayList<>(todasEquipas);
    // }
    
    // public static void limparEquipas() {
    //     todasEquipas.clear();
    // }

    

    // @Override
    // public String toString() {
    //     return getStatusEquipa();
    // }
}