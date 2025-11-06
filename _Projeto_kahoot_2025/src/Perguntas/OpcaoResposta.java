package Perguntas;

public class OpcaoResposta {
	
	private String texto;
    private boolean correta;

    public OpcaoResposta(String texto, boolean correta) {
        this.texto = texto;
        this.correta = correta;
    }

    public String getTexto() {
        return texto;
    }

    public boolean isCorreta() {
        return correta;
    }

}
