package Protocolos;

public class ErrorResponse extends Mensagem {
    private static final long serialVersionUID = 1L;
    private final String error;

    public ErrorResponse(String error) { this.error = error; }

    public String getError() { return error; }
}
