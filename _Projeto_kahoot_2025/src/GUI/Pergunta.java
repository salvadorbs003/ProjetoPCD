package GUI;

import java.util.List;

public class Pergunta {
	
	private String question;  // nome vem do JSON
    private List<String> options;
    private int correct;
    private int points;

    private int id;
    private int tempo = 30; 

    public String getTexto() {
        return question;
    }

    public List<String> getOpcoes() {
        return options;
    }

    public int getIndiceCorreto() {
        return correct;
    }

    public int getPontos() {
        return points;
    }

    public int getTempo() {
        return tempo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}