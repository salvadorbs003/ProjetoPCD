package Protocolos;

import java.io.Serializable;

public class CheckPlayerResponse extends Mensagem{
    private static final long serialVersionUID = 1L;
    
    public enum Status{
        OK, ERROR
    }

    private final Status status;
    private final String msg;

    public CheckPlayerResponse(Status status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public Status getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }


    public static CheckPlayerResponse ok(String msg) {
        return new CheckPlayerResponse(Status.OK, msg);
    }

    public static CheckPlayerResponse error(String msg) {
        return new CheckPlayerResponse(Status.ERROR, msg);
    }

    public boolean isOk() {
        return status == Status.OK;
    }
}
