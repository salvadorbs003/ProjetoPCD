package Cliente;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Protocolos.CheckPlayerRequest;
import Protocolos.CheckPlayerResponse;
import Protocolos.CheckSalaRequest;
import Protocolos.CheckSalaResponse;
import Protocolos.ErrorResponse;
import Protocolos.GameStartRequest;
import Protocolos.JoinRequest;
import Protocolos.JoinResponse;
import Protocolos.Mensagem;
import Protocolos.NextQuestionRequest;
import Protocolos.StartNotification;
import Protocolos.SubmitAnswerRequest;
import Protocolos.TeamStatusRequest;
import Protocolos.TeamStatusResponse;

public class Cliente {
    private String host;
    private int port;
    private String pin;
    private String equipa;
    private String nome;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public Cliente(String host, int port, String pin, String equipa, String nome) {
        this.host = host;
        this.port = port;
        this.pin = pin;
        this.equipa = equipa;
        this.nome = nome;
    }

    private boolean abrirLigacao() throws IOException {
        socket = new Socket(host, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
        return true;
    }

    public boolean ligar() {
        System.out.println("Im trying to connect to the server!");
        try {
            abrirLigacao();
            out.writeObject(new JoinRequest(pin, nome, equipa));
            out.flush();

            while (true) {
                Object answer = in.readObject();
                if (answer instanceof JoinResponse joinResp) {
                    if (!joinResp.isOk()) {
                        System.err.println("Join falhou: " + joinResp.getMsg());
                        return false;
                    }
                    System.out.println("Join ok: " + joinResp.getMsg());
                } else if (answer instanceof TeamStatusResponse) {
                    System.out.println("Estado equipa: " + ((TeamStatusResponse) answer).getEquipaNome());
                } else if (answer instanceof StartNotification) {
                    return true; // Game Started
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao ligar ao servidor: " + e.getMessage());
            return false;
        }
    }

    public boolean validarSala() {
        try (Socket s = new Socket(host, port);
             ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream i = new ObjectInputStream(s.getInputStream())) {
            
            o.writeObject(new CheckSalaRequest(pin));
            o.flush();
            Object obj = i.readObject();
            return (obj instanceof CheckSalaResponse resp) && resp.isOk();
        } catch (Exception e) {
            return false;
        }
    }

    public int verificarEstadoEquipa(String nomeEquipa) {
        try (Socket s = new Socket(host, port);
             ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream i = new ObjectInputStream(s.getInputStream())) {
            
            o.writeObject(new TeamStatusRequest(pin, nomeEquipa, nome));
            o.flush();
            Object obj = i.readObject();
            if (obj instanceof TeamStatusResponse resp) {
                return resp.getPlayerCount();
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public boolean verificarJogador(String jogadorNome, String pin) {
        try (Socket s = new Socket(host, port);
             ObjectOutputStream o = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream i = new ObjectInputStream(s.getInputStream())) {
            
            o.writeObject(new CheckPlayerRequest(jogadorNome, pin));
            o.flush();
            Object obj = i.readObject();
            return (obj instanceof CheckPlayerResponse resp) && resp.isOk();
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized Mensagem enviarGameStartRequest(String pin) {
        System.out.println("Sending GameStartRequest...");
        try {
            // Using 0 for qnum as server decides random
            out.writeObject(new GameStartRequest(pin, 45, 0));
            out.flush();

            System.out.println("Waiting for NextQuestion...");
            while (true) {
                Object response = in.readObject();
                if (response instanceof Mensagem) {
                    return (Mensagem) response;
                }
                System.out.println("⚠️ Ignoring unexpected message: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String enviarResposta(int index) {
        System.out.println(">>> [DEBUG] Sending Answer Index: " + index);
        try {
            out.writeObject(new SubmitAnswerRequest(this.pin, this.nome, this.equipa, index));
            out.flush();

            Object response = in.readObject();
            if (response instanceof ErrorResponse) {
                return ((ErrorResponse) response).getError();
            }
            return "Erro desconhecido.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro de comunicação.";
        }
    }

    public Mensagem pedirProximaPergunta(int roundAtual) {
        System.out.println(">>> [DEBUG] Asking for Next Question (After Round " + roundAtual + ")");
        try {
            out.writeObject(new NextQuestionRequest(pin, roundAtual));
            out.flush();

            Object response = in.readObject();
            if (response instanceof Mensagem) {
                return (Mensagem) response;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void fechar() {
        try { if (out != null) out.close(); } catch (Exception ignored) {}
        try { if (in != null) in.close(); } catch (Exception ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (Exception ignored) {}
    }

    public String getNome() {
        return nome;
    }
}