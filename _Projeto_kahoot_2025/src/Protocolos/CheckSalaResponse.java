package Protocolos;

public class CheckSalaResponse {

    public enum Status{
        OK, ERROR
    }

    private final Status status;
    private final String msg;

    public CheckSalaResponse(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static CheckSalaResponse ok(String msg) {
        return new CheckSalaResponse(Status.OK, msg);
    }

    public static CheckSalaResponse error(String msg) {
        return new CheckSalaResponse(Status.ERROR, msg);
    }

    public boolean isOk() {
        return status == Status.OK;
    }

     public String serialize() {
        String prefix = status == Status.OK ? "SALA_OK" : "SALA_ERROR";
        return msg == null || msg.isBlank() ? prefix : prefix + " " + msg;
    }

//Static helpers
    
    //checks if the response belongs to CHECK_SALA command
    public static boolean matches(String linha){
        return linha != null && (linha.startsWith("SALA_OK") || linha.startsWith("SALA_ERROR"));
    }

    //Forms an object of type JoinResponse 
    public static CheckSalaResponse fromRaw(String linha){
        if (!matches(linha)) {
            throw new IllegalArgumentException("Mensagem não é CHECK_SALA");
        }

        boolean ok = linha.startsWith("SALA_OK");
        String prefix = ok ? "SALA_OK" : "SALA_ERROR";
        String detalhe = linha.length() > prefix.length() ?
             linha.substring(prefix.length()).trim()
            : "";
        return new CheckSalaResponse(ok ? Status.OK : Status.ERROR, detalhe);
    }

}