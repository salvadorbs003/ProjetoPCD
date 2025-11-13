
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
    
    public boolean existsPlayer(String nome){
        for (Jogador jogador : jogadores) {
            if(jogador.getNome().equals(nome)){
                return true;
            }
        }
        return false;
    }
}