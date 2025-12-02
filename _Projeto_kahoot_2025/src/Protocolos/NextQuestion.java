package Protocolos;

import java.util.List;

public class NextQuestion extends Mensagem {
    private final String pinSala;
    private final int round;
    private final int questionId;
    private final String questionText;
    private final List<String> options;
    private final int timeLimitSeconds;
    private final List<PlayerScore> scoreboard;
    private final int totalQuestions;

    public NextQuestion(String pinSala, int round, int questionId, String questionText,
                             List<String> options, int timeLimitSeconds,
                             List<PlayerScore> scoreboard, int totalQuestions) {
        this.pinSala = pinSala;
        this.round = round;
        this.questionId = questionId;
        this.questionText = questionText;
        this.options = options;
        this.timeLimitSeconds = timeLimitSeconds;
        this.scoreboard = scoreboard;
        this.totalQuestions = totalQuestions;
    }
    
    public static class PlayerScore implements java.io.Serializable {
        private final String teamName;
        private final String playerName;
        private final int points;

        public PlayerScore(String teamName, String playerName, int points) {
            this.teamName = teamName;
            this.playerName = playerName;
            this.points = points;
        }
        public String getTeamName() { return teamName; }
        public String getPlayerName() { return playerName; }
        public int getPoints() { return points; }
    }
    
}
