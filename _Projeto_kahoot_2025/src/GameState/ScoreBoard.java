package GameState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScoreBoard implements Serializable {
    private static final long serialVersionUID = 1L;

    // Enum to tell the Client which ranking to show (Players or Teams)
    public enum ScoreType {
        INDIVIDUAL,
        TEAM
    }

    private final ScoreType type;
    
    // We calculate and store BOTH lists so they are ready to send
    private final List<PlayerScore> playerScores;
    private final List<TeamScore> teamScores;

    /**
     * Constructor: Creates a snapshot of the current scores.
     * @param teams The live list of teams from GameState
     * @param type The mode for this specific round (decides what the GUI shows by default)
     */
    public ScoreBoard(Collection<Equipa> teams, ScoreType type) {
        this.type = type;
        this.playerScores = new ArrayList<>();
        this.teamScores = new ArrayList<>();

        if (teams != null) {
            calculateScores(teams);
        }
    }

    // Logic to flatten the data into simple lists
    private void calculateScores(Collection<Equipa> teams) {
        for (Equipa equipa : teams) {
            int currentTeamTotal = 0;

            // 1. Extract Player Scores
            for (Jogador j : equipa.getJogadores()) {
                // Add to player leaderboard
                playerScores.add(new PlayerScore(
                    equipa.getNome(), 
                    j.getNome(), 
                    j.getPontuacao()
                ));
                
                // Sum up points for the team leaderboard
                currentTeamTotal += j.getPontuacao();
            }

            // 2. Create Team Score
            teamScores.add(new TeamScore(equipa.getNome(), currentTeamTotal));
        }

        // 3. Sort both lists (Highest points at the top)
        playerScores.sort(Comparator.comparingInt(PlayerScore::getPoints).reversed());
        teamScores.sort(Comparator.comparingInt(TeamScore::getPoints).reversed());
    }

    // --- Getters ---

    public ScoreType getType() {
        return type;
    }

    public List<PlayerScore> getPlayerScores() {
        return Collections.unmodifiableList(playerScores);
    }

    public List<TeamScore> getTeamScores() {
        return Collections.unmodifiableList(teamScores);
    }

    // --- Simple DTOs (Data Transfer Objects) to send over network ---

    public static class PlayerScore implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String teamName;
        private final String playerName;
        private final int points;

        public PlayerScore(String teamName, String playerName, int points) {
            this.teamName = teamName;
            this.playerName = playerName;
            this.points = points;
        }

        public String getPlayerName() { return playerName; }
        public String getTeamName() { return teamName; }
        public int getPoints() { return points; }
        
        @Override
        public String toString() { return playerName + ": " + points; }
    }

    public static class TeamScore implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String teamName;
        private final int points;

        public TeamScore(String teamName, int points) {
            this.teamName = teamName;
            this.points = points;
        }

        public String getTeamName() { return teamName; }
        public int getPoints() { return points; }

        @Override
        public String toString() { return teamName + ": " + points; }
    }
}