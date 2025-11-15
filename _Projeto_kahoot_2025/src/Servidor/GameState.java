package Servidor;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Quizz.Pergunta;
import GameState.Equipa;
import GameState.Jogador;

public class GameState {
	
	private String codigoPIN;
    private final HashMap<String, Equipa> teams = new HashMap<>();
    private List<Pergunta> perguntas;

    public GameState(String pin, List<Pergunta> perguntas) {
        this.codigoPIN = pin;
        this.perguntas = perguntas;
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

    public synchronized boolean canStart() {
        if (teams.size() < 2) {
            return false; // Precisa de pelo menos 2 equipas
        }
        
        int equipasCompletas = 0;
        for (Equipa equipa : teams.values()) {
            if (equipa.estaCompleta()) {
                equipasCompletas++;
            }
        }
        
        return equipasCompletas >= 2; // 2 equipas completas
        
        
    }

    public synchronized Equipa getEquipa(String nome){
        return teams.get(nome);
    }

    public synchronized void limpar() {
        teams.clear(); // removes every entry from the map
    }

    // Retorna uma cópia das equipas da sala para que os handlers possam apresentar estado
    public synchronized List<Equipa> listarEquipas() {
        return new ArrayList<>(teams.values());
    }
    public synchronized int getNumeroJogadoresEquipa(String nomeEquipa) {
        Equipa equipa = teams.get(nomeEquipa);
        if (equipa == null) {
            return 0;
        }
        return equipa.getNumeroJogadores();
    }
     
}