package Protocolos;

public class CheckSalaRequest {
    private final String pin;

    public CheckSalaRequest(String pin) {
        this.pin = pin;
    }

    public String getPin() {
        return pin;
    }

    public String serialize() {
        return "CHECK_SALA " + pin;
    }

    public static boolean matches(String linha) { return linha != null && linha.startsWith("CHECK_SALA"); }

    public static CheckSalaRequest fromRaw(String linha) { 
        if (!matches(linha)) {
            throw new IllegalArgumentException("Mensagem não é CHECK_SALA");
        }

        String[] partes = linha.trim().split("\\s+", 2);
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato CHECK_SALA inválido");
        }
        return new CheckSalaRequest(partes[1]); 
    }
    
}
