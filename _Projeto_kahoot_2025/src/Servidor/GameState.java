package Servidor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import GameState.Equipa;
import GameState.Jogador;
import GameState.ScoreBoard;
import HomeMade.GroupBarrier;
import HomeMade.IndiSem;
import HomeMade.RoundLatch;
import Quizz.Pergunta;

public class GameState {

    private String codigoPIN;
    private final HashMap<String, Equipa> teams = new HashMap<>();
    private List<Pergunta> perguntas;
    
    // Game State variables
    private int round = 0;
    private List<Pergunta> availableQ;
    private Pergunta currentPergunta;
    private boolean roundInitialized = false;

    // Synchronization primitives
    private RoundLatch currentRoundLatch;
    private IndiSem currentIndiSem;
    private Map<String, GroupBarrier> teamBarriers = new HashMap<>();

    public GameState(String pin, List<Pergunta> perguntas) {
        this.codigoPIN = pin;
        this.perguntas = perguntas;
    }

    public synchronized void setAvailableQ(int question) {
        // 1. Initialize list if it's the first round
        if (round == 0 || availableQ == null) {
            availableQ = new ArrayList<>(perguntas);
        }
        
        if (availableQ.isEmpty()) {
            System.out.println("No more questions available.");
            this.currentPergunta = null;
            return;
        }

        // 2. Pick a random question
        int indexToPick = ThreadLocalRandom.current().nextInt(availableQ.size());
        this.currentPergunta = availableQ.get(indexToPick);

        // 3. Randomly assign Type (for testing purposes)
        boolean isGroup = ThreadLocalRandom.current().nextBoolean();
        this.currentPergunta.setType(isGroup ? Pergunta.Type.GROUP : Pergunta.Type.INDIVIDUAL);
        
        System.out.println("Selected Question ID: " + currentPergunta.getId());
        System.out.println("Assigned Type: " + currentPergunta.getType());

        // 4. Remove so it's not repeated
        availableQ.remove(indexToPick);
    }

    public synchronized void prepareNextRound(int time) {
        if (currentPergunta == null) return;

        int totalPlayers = teams.values().stream().mapToInt(Equipa::getNumeroJogadores).sum();
        long timeoutMillis = time * 1000L;

        // Global latch for all players to finish answering
        this.currentRoundLatch = new RoundLatch(timeoutMillis, totalPlayers);

        if (currentPergunta.getType() == Pergunta.Type.INDIVIDUAL) {
            this.currentIndiSem = new IndiSem(timeoutMillis, totalPlayers);
            this.teamBarriers.clear();
        } 
        else if (currentPergunta.getType() == Pergunta.Type.GROUP) {
            this.currentIndiSem = null;
            this.teamBarriers.clear();

            for (Equipa team : teams.values()) {
                // Creates a barrier for each team
                GroupBarrier barrier = new GroupBarrier(timeoutMillis, team.getNumeroJogadores());
                teamBarriers.put(team.getNome(), barrier);
            }
        }
        this.roundInitialized = true;
        round++;
    }

    // --- HELPER METHODS ---

    public synchronized boolean addEquipa(String teamName, Jogador j) {
        Equipa team = teams.get(teamName);
        if (team == null) {
            if (teams.size() >= 2) return false; // Simple limit
            team = new Equipa(teamName);
            teams.put(teamName, team);
        }
        return team.adicionarJogador(j);
    }

    public synchronized ScoreBoard getScoreboard() {
        ScoreBoard.ScoreType type = ScoreBoard.ScoreType.INDIVIDUAL;
        if (currentPergunta != null && currentPergunta.getType() == Pergunta.Type.GROUP) {
            type = ScoreBoard.ScoreType.TEAM;
        }
        return new ScoreBoard(teams.values(), type);
    }

    public synchronized boolean existeJogador(String nome) {
        return teams.values().stream()
                .flatMap(e -> e.getJogadores().stream())
                .anyMatch(j -> j.getNome().equalsIgnoreCase(nome));
    }

    public synchronized boolean canStart() {
        if (teams.size() < 2) return false;
        int equipasCompletas = 0;
        for (Equipa equipa : teams.values()) {
            if (equipa.estaCompleta()) equipasCompletas++;
        }
        return equipasCompletas >= 2;
    }

    // --- GETTERS ---
    public synchronized Equipa getEquipa(String nome) { return teams.get(nome); }
    public String getCodigoPIN() { return codigoPIN; }
    public List<Pergunta> getPerguntas() { return perguntas; }
    public Pergunta getCurrentPergunta() { return currentPergunta; }
    public synchronized RoundLatch getCurrentRoundLatch() { return currentRoundLatch; }
    public synchronized IndiSem getCurrentIndiSem() { return currentIndiSem; }
    public synchronized Map<String, GroupBarrier> getTeamBarriers() { return teamBarriers; }
    public boolean isRoundInitialized() { return roundInitialized; }
    public int getRound() { return round; }
}