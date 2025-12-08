package Protocolos;

public final class CheckSalaRequest extends Mensagem{
    private static final long serialVersionUID = 1L;
    private final String pin;

    public CheckSalaRequest(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    } 
}