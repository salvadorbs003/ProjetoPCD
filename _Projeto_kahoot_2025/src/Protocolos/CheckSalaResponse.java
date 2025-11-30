package Protocolos;

import java.io.Serializable;

public final class CheckSalaResponse extends Mensagem{
    private static final long serialVersionUID = 1L;

    public enum Status{
        OK, ERROR
    }

    private final Status status;
    private final String msg;

    public CheckSalaResponse(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
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

}