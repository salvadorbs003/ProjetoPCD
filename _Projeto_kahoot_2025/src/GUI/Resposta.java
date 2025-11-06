package GUI;

public class Resposta {
	
	private int jogadorId;           // id do jogador
    private int perguntaId;          // id da pergunta
    private String respostaTexto;    // opcao de resposta desta pergunta escolhida
    private boolean correta;         // se a resposta estava certa
    private int pontosObtidosDestaPergunta;       // pontos ganhos nesta pergunta

    public Resposta(int jogadorId, int perguntaId, String respostaTexto, boolean correta, int pontosObtidos) {
        this.jogadorId = jogadorId;
        this.perguntaId = perguntaId;
        this.respostaTexto = respostaTexto;
        this.correta = correta;
        this.pontosObtidosDestaPergunta = pontosObtidos;
    }

    public int getJogadorId() {
        return jogadorId;
    }

    public int getPerguntaId() {
        return perguntaId;
    }

    public String getRespostaTexto() {
        return respostaTexto;
    }

    public boolean isCorreta() {
        return correta;
    }

    public int getPontosObtidos() {
        return pontosObtidosDestaPergunta;
    }

 

}
