package Protocolos;


public class MensagemChat extends Mensagem {
    private static final long serialVersionUID = 1L;
    
    private final String autor;
    private final String texto;

    public MensagemChat(String autor, String texto) {
        this.autor = autor;
        this.texto = texto;
    }

    public String getAutor() { return autor; }
    public String getTexto() { return texto; }

    @Override
    public String toString() {
        return autor + ": " + texto;
    }
}
