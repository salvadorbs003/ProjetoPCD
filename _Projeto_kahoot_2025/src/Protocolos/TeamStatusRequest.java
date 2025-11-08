package Protocolos;

public class TeamStatusRequest {
    private final String pinSala;
    private final String equipaNome;

    public TeamStatusRequest(String pinSala, String equipaNome) {
        this.pinSala = pinSala;
        this.equipaNome = equipaNome;
    }

    public String getPinSala() {
        return pinSala;
    }

    public String getEquipaNome() {
        return equipaNome;
    }

    public String serialize() {
        return "CHECK_EQUIPA " + pinSala + " " + equipaNome;
    }

    public static boolean matches(String linha) { return linha != null && linha.startsWith("CHECK_EQUIPA"); }

    public static TeamStatusRequest fromRaw(String linha) { 
        if (!matches(linha)) {
            throw new IllegalArgumentException("Mensagem não é CHECK_EQUIPA");
        }

        String[] partes = linha.trim().split("\\s+", 3);
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato CHECK_EQUIPA inválido");
        }
        return new TeamStatusRequest(partes[1], partes[2]); 
    }
}
