package Cliente;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import GUI.Pergunta;
import GameState.Equipa;
import GameState.Jogador;

public class GameState {
	
	private String codigoPIN;
    private final HashMap<String, Equipa> teams = new HashMap<>();
    private List<Jogador> jogadores = new ArrayList<>(); 
    private List<Pergunta> perguntas;

    public GameState(String pin, List<Pergunta> perguntas) {
        this.codigoPIN = pin;
        this.perguntas = perguntas;
    }
    public synchronized void adicionarJogador(Jogador j) {
        jogadores.add(j);
    }

    
    public synchronized boolean addEquipa(String teamName, Jogador j) {
        Equipa team = teams.get(teamName);
        if(team == null){
            if(teams.size() >=2){
                System.err.println("Já não podem ser adicionads equipas a esta sala");
                return false;
            }
            team = new Equipa(teamName);
            teams.put(teamName, team);
        }
        return team.adicionarJogador(j);
        
    }
    
    //access team.values get teams list of players 
    //and check if that name already exists
    public synchronized boolean existeJogador(String nome) {
        return teams.values().stream()
                        .flatMap(e -> e.getJogadores().stream())
                        .anyMatch(j -> j.getNome().equalsIgnoreCase(nome));
    }

    public synchronized boolean canStart(){
        if (teams.size() != 2) {
        return false;
    }
    for (Equipa equipa : teams.values()) {
        if(equipa.getNumeroJogadores() != 2){
            return false;
        }
        
    }
    return true;
    }
}