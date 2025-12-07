package Protocolos;

import java.io.Serializable;
import java.util.List;

public class GameStartRequest extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;
    private final int round = 1;
    private final int qnum;
    // private final int totalQuestions;
    // private final String questionText;
    // private final List<String> options;
    private final int timeLimitSeconds;
    //private final List<PlayerScore> scoreboard; //to be understood
    // private final String namePlayer;
    // private final String nameTeam;
    
    public GameStartRequest(String pinSala,int timeLimitSeconds, int qnum) {
            this.pinSala = pinSala;
            // this.totalQuestions = totalQuestions;
            // this.questionText = questionText;
            // this.options = options;
            this.timeLimitSeconds = timeLimitSeconds;
            //this.scoreboard = scoreboard;
            // this.namePlayer = namePlayer;
            // this.nameTeam = nameTeam;
            this.qnum = qnum;
        }
        
        public String getPinSala() {
            return pinSala;
        }
        
        // public String getNamePlayer() {
        //     return namePlayer;
        // }
        
        // public String getNameTeam() {
        //     return nameTeam;
        // }
        
        // public int getTotalQuestions() {
        //     return totalQuestions;
        // }
        
        // public String getQuestionText() {
        //     return questionText;
        // }
        
        // public List<String> getOptions() {
        //     return options;
        // }
        
        public int getTimeLimitSeconds() {
            return timeLimitSeconds;
        }
        
        // public List<PlayerScore> getScoreboard() {
        //     return scoreboard;
        // }

        public int getQuestion() {
            return qnum;
        }
    }