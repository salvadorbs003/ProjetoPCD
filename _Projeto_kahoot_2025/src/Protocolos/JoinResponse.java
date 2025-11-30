package Protocolos;

public final class JoinResponse extends Mensagem {
    private static final long serialVersionUID = 1L;

    public enum Status {
        OK, ERROR
    }

    private final Status status;
    private final String msg;

    public JoinResponse(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
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

    @Override
    public String toString() {
        return "JoinResponse{" +
                "estado='" + status + '\'' +
                ", mensagem='" + msg + '\'' +
                '}';
    }
}
