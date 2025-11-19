package Quizz;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Quiz {
    @SerializedName(value = "name", alternate = {"nome", "titulo"})
    private String nomeQuiz;

    @SerializedName(value = "questions", alternate = {"perguntas"})
    private List<Pergunta> perguntas;

    // Allow Gson to build the object and map JSON keys onto the private fields
    public Quiz() {
    }

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
