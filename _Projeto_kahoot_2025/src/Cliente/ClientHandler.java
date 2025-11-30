package Cliente;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Protocolos.Mensagem;
import Servidor.Servidor;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private String pinSala;
    private String nomeJogador;
    private String equipaNome;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            initializeStreams();

            while (true) {
                Object obj = in.readObject();

                if (!(obj instanceof Mensagem msg)) {
                    System.err.println("⚠️ Objeto inválido recebido do cliente: " + obj);
                    continue;
                }

                Servidor.processMsg(this, msg);
            }

        } catch (EOFException ignored) {
            System.out.println("Cliente desligou: " + socket);
        } catch (Exception e) {
            System.err.println("Erro no handler: " + e.getMessage());
        } finally {
            closeStreams();
        }
    }

    private void initializeStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    public void enviarMensagem(Mensagem mensagem) {
        try {
            synchronized (out) {
                out.writeObject(mensagem);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Falha ao enviar: " + e.getMessage());
        }
    }

    private void closeStreams() {
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (!socket.isClosed()) socket.close(); } catch (Exception ignored) {}
    }

    // GETTERS
    public String getPinSala() { return pinSala; }
    public String getNomeJogador() { return nomeJogador; }
    public String getEquipaNome() { return equipaNome; }

    // SETTER DO CONTEXTO
    public void setContext(String pinSala, String nomeJogador, String equipaNome) {
        this.pinSala = pinSala;
        this.nomeJogador = nomeJogador;
        this.equipaNome = equipaNome;
    }
}
