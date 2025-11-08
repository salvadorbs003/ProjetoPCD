package Protocolos;

public class JoinResponse {

    public enum Status{
        OK, ERROR
    }

    private final Status status;
    private final String msg;
    
    public JoinResponse(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public static JoinResponse ok(String msg) {
        return new JoinResponse(Status.OK, msg);
    }

    public static JoinResponse error(String msg) {
        return new JoinResponse(Status.ERROR, msg);
    }

    public boolean isOk() {
        return status == Status.OK;
    }

    public String serialize() {
        String prefix = status == Status.OK ? "JOIN_OK" : "JOIN_ERROR";
        return msg == null || msg.isBlank() ? prefix : prefix + " " + msg;
    }
//Static helpers
    
    //checks if the response belongs to JOIN command
    public static boolean matches(String linha){
        return linha != null && (linha.startsWith("JOIN_OK") || linha.startsWith("JOIN_ERROR"));
    }

    //Forms an object of type JoinResponse 
    public static JoinResponse formJoin(String linha){
        if (!matches(linha)) {
            throw new IllegalArgumentException("Mensagem não é JOIN");
        }

        boolean ok = linha.startsWith("JOIN_OK");
        String prefix = ok ? "JOIN_OK" : "JOIN_ERROR";
        String detalhe = linha.length() > prefix.length() ?
             linha.substring(prefix.length()).trim()
            : "";
        return new JoinResponse(ok ? Status.OK : Status.ERROR, detalhe);
    }

//ToString method
    @Override
    public String toString() {
        return "JoinResponse{" +
                "estado='" + status + '\'' +
                ", mesnagem='" + msg + '\'' +
                '}';
    }
    
}
