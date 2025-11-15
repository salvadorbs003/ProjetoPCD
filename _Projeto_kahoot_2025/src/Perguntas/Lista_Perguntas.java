package Perguntas;

import java.util.ArrayList;
import java.util.List;

import Quizz.Pergunta;

public class Lista_Perguntas {

    private static List<Pergunta> perguntas = new ArrayList<>();

    public static void adicionarPergunta(Pergunta pergunta) {
        perguntas.add(pergunta);
    }

    public static List<Pergunta> getPerguntas() {
        return perguntas;
    }

    // vai buscar a pergunta pelo ID
    public static Pergunta getPerguntaPorId(int id) {
        return perguntas.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public static void limparPerguntas() {
        perguntas.clear();
    }
    
    public static void definirPerguntas(List<Pergunta> lista) {
        perguntas.clear();
        if (lista != null) {
            for (int i = 0; i < lista.size(); i++) {
                Pergunta p = lista.get(i);
                p.setId(i + 1); //  atribui ID sequencial
                perguntas.add(p);
            }
        }
    }
}
