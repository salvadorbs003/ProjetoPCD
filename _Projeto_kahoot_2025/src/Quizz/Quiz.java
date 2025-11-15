package Quizz;

import java.util.List;

public class Quiz {
    private final String nomeQuiz;
    private final List<Pergunta> perguntas;
    
    public Quiz(String nomeQuiz, List<Pergunta> perguntas) {
        this.nomeQuiz = nomeQuiz;
        this.perguntas = perguntas;
    }
    
    public String getNomeQuiz() {
        return nomeQuiz;
    }
    public List<Pergunta> getPerguntas() {
        return perguntas;
    }
    
}
