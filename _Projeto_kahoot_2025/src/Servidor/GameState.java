package Servidor;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GameState.Equipa;
import GameState.Jogador;
import HomeMade.GroupBarrier;
import HomeMade.IndiSem;
import HomeMade.RoundLatch;
import Quizz.Pergunta;
import GameState.ScoreBoard;

public class GameState {
	
	private String codigoPIN;
    private final HashMap<String, Equipa> teams = new HashMap<>();
    private List<Pergunta> perguntas;
    private int currentQ;
    private int round = 1;
    private List<Pergunta> availableQ;
    private Pergunta currentPergunta;
    private ScoreBoard scoreboard;

    private RoundLatch currentRoundLatch;
    private IndiSem currentIndiSem;
    private Map<String, GroupBarrier> teamBarriers = new HashMap<>();
    private boolean roundInitialized = false;

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
    
    public synchronized ScoreBoard getScoreboard() {
    // Determine type: If current question is GROUP, show Team Scores
    ScoreBoard.ScoreType type = ScoreBoard.ScoreType.INDIVIDUAL;
    
    if (currentPergunta != null && currentPergunta.getType() == Pergunta.Type.GROUP) {
        type = ScoreBoard.ScoreType.TEAM;
    }

    // This constructor grabs the CURRENT points (0 at start) and puts them in the list
    return new ScoreBoard(teams.values(), type); 
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
     
    public List<Pergunta> getPerguntas() {
        return perguntas;
    }

    public int getCurrentQ() {
        return currentQ;
    }

    

    public Pergunta getCurrentPergunta() {
        return currentPergunta;
    }


    public synchronized void prepareNextRound(int time){
        if(round >= perguntas.size()) return;
        
        Pergunta p = availableQ.get(currentQ);
        int totaPlayers = 4; //eventually its going to not be hard coded
        long timeoutMillis = time * 1000L;

        this.currentRoundLatch = new RoundLatch(timeoutMillis, totaPlayers);

        if(p.getType() == Pergunta.Type.INDIVIDUAL){
            this.currentIndiSem = new IndiSem(timeoutMillis, totaPlayers);
            this.teamBarriers.clear();
        }
        else if(p.getType() == Pergunta.Type.GROUP){
            this.currentIndiSem = new IndiSem(timeoutMillis, totaPlayers);
            this.teamBarriers.clear();

            for (Equipa team : teams.values()) {
                Runnable barrierAction = () -> {
                    System.out.println("Team " + team.getNome() + " finished answering!");
                    // Logic to calculate team score could go here
                };

                GroupBarrier barrier = new GroupBarrier(
                        timeoutMillis, 
                        team.getNumeroJogadores(), 
                        barrierAction
                    );
                teamBarriers.put(team.getNome(), barrier);
            }

        }
        this.roundInitialized = true;
        round++; //not sure abt this tho
    }

    public synchronized RoundLatch getCurrentRoundLatch() {
        return currentRoundLatch;
    }


    public synchronized IndiSem getCurrentIndiSem() {
        return currentIndiSem;
    }


    public synchronized Map<String, GroupBarrier> getTeamBarriers() {
        return teamBarriers;
    }


    public boolean isRoundInitialized() {
        return roundInitialized;
    }

    public int getRound() {
        return round;
    }

    public synchronized void setAvailableQ(int question){
        if(round == 1 || availableQ == null){
            availableQ= new ArrayList<>(perguntas);
        }
        if(availableQ.isEmpty()) return;

        int indexToPick = question;
        // 3. GET the question first
        this.currentPergunta = availableQ.get(indexToPick);
        this.currentQ = indexToPick; 

        // 4. REMOVE it so it's not available next time
        availableQ.remove(indexToPick);
        
        System.out.println("Selected Question: " + currentPergunta.getQuestion());
        System.out.println("Questions remaining for next round: " + availableQ.size());
    }

}