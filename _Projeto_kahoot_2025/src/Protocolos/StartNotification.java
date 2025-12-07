// file: src/Protocolos/StartNotification.java
package Protocolos;

public class StartNotification extends Mensagem {
    private static final long serialVersionUID = 1L;

    private final String pinSala;
    private final int question; // optional countdown hint

    public StartNotification(String pinSala, int question) {
        this.pinSala = pinSala;
        this.question = question;
    }

    public String getPinSala() { return pinSala; }
    public int getQuestion() { return question; }

    @Override
    public String toString() {
        return "StartNotification{pin=" + pinSala + ", question number: " + question + "}";
    }
}
