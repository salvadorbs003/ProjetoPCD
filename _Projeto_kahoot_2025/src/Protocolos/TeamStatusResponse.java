package Protocolos;

public class TeamStatusResponse {
    public enum Status {
        COMPLETA, INCOMPLETA
    }

    private final String equipaNome;
    private final Status status; 
    private final int playercount;
    private static final int MAX_PLAYERS = 2;

    public TeamStatusResponse(String equipaNome, Status status, int playercount) {
        this.equipaNome = equipaNome;
        this.status = status;
        this.playercount = playercount;
    }

    public String getEquipaNome() {
        return equipaNome;
    }
    public Status getStatus() {
        return status;
    }

    public int getPlayerCount() {
        return playercount;
    }

    public static TeamStatusResponse completa(String equipaNome, int playercount) {
        return new TeamStatusResponse(equipaNome, Status.COMPLETA, playercount);
    }
    public static TeamStatusResponse incompleta(String equipaNome, int playercount) {
        return new TeamStatusResponse(equipaNome, Status.INCOMPLETA, playercount);
    }

    public boolean isCompleta() {
        return status == Status.COMPLETA;
    }

    public String serialize() {
        String statusToken = status == Status.COMPLETA ? "EQUIPA_COMPLETA" : "EQUIPA_INCOMPLETA";
        return statusToken + " " + equipaNome + " " + playercount + "/" + MAX_PLAYERS;
    }

//Static helpers
    
    public static boolean matches(String linha){
        return linha != null && (linha.startsWith("EQUIPA_INCOMPLETA") || linha.startsWith("EQUIPA_COMPLETA"));
    }

    //Forms an object of type JoinResponse 
    public static TeamStatusResponse fromRaw(String linha) {
        if (!matches(linha)) {
            throw new IllegalArgumentException("Mensagem não é TeamStatus");
        }

        boolean completa = linha.startsWith("EQUIPA_COMPLETA");
        String[] partes = linha.trim().split("\\s+", 3);
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato TeamStatus inválido");
        }

        String equipaNome = partes[1];
        String[] contagem = partes[2].split("/");
        int playerCount = Integer.parseInt(contagem[0]);

        return new TeamStatusResponse(
            equipaNome,
            completa ? Status.COMPLETA : Status.INCOMPLETA,
            playerCount
        );
    }

}
