package Quizz;

import java.io.Serializable;
import java.util.List;

public class Pergunta implements Serializable{
	private static final long serialVersionUID = 1L; // Recommended

	 public enum Type{
	        INDIVIDUAL, GROUP
	    }

	    private int id; // Identificador sequencial opcional para a pergunta
	    private Type type;
	    private String question;  
	    private List<String> options;
	    private int correct;
	    private int points;
	    private int tempo = 30; // default seconds for answering when JSON omits tempo

	    
	    public Pergunta(String question, List<String> options, int correct, int points) {
	        this.question = question;
	        this.options = options;
	        this.correct = correct;
	        this.points = points;
	    }
	    
	    public Type getType() {
	        return type;
	    }
	    public void setType(Type type) {
	        this.type = type;
	    }
	    public String getQuestion() {
	        return question;
	    }
	    public List<String> getOptions() {
	        return options;
	    }
	    public void setOptions(List<String> options) {
	        this.options = options;
	    }
	    public int getCorrect() {
	        return correct;
	    }

	    public int getPoints() {
	        return points;
	    }

	    public int getTempo() {
	        return tempo > 0 ? tempo : 30; // keep a sensible fallback even if JSON left it zero
	    }

	    public void setTempo(int tempo) {
	        this.tempo = tempo;
	    }

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }

	    // Legacy-style getters used pela camada GUI
	    public String getTexto() {
	        return getQuestion();
	    }

	    public List<String> getOpcoes() {
	        return getOptions();
	    }


		@Override
		public String toString() {
			return "Pergunta{" +
					"id=" + id +
					", type=" + type +
					", question='" + question + '\'' +
					", options=" + options +
					", correct=" + correct +
					", points=" + points +
					", tempo=" + tempo +
					'}';
		}
	
}
