package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import GameState.Jogador;
import GameState.Equipa;
import Protocolos.CheckSalaRequest;
import Protocolos.CheckSalaResponse;
import Protocolos.JoinRequest;
import Protocolos.JoinResponse;
import Protocolos.TeamStatusRequest;
import Protocolos.TeamStatusResponse;
import Servidor.GameState;
import Servidor.Servidor;

public class ClientHandler implements Runnable {
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String pinSala;
    private String nomeJogador;
    private String equipaNome;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    public void enviarMensagem(String mensagem) {
        if (out != null) {
            out.println(mensagem);
        }
    }

    //Doesnt make a lot of sense to handle everything on the run 
    //seems cluttered, the code is not clean or nice to look at 
    //and might originate some unexpected errors

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Bem-vindo ao IsKahoot!");
            // Cada socket fala o protocolo textual; transformamos cada linha no respetivo
            // objeto (JoinRequest, TeamStatusRequest, etc.) antes de agir sobre o GameState.
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Mensagem do cliente: " + msg);
                //I think it should be a separeted method that listens for the messages
                //and is called by run() 
                if (JoinRequest.matches(msg)) {
                    JoinRequest join;
                    try {
                        join = JoinRequest.formJoin(msg);
                    } catch (IllegalArgumentException e) {
                        out.println(JoinResponse.error("Formato inválido! Usa: JOIN <PIN> <Equipa> <Nome>").serialize());
                        continue;
                    }
                        
                    this.pinSala = join.getPinSala();
                    this.nomeJogador = join.getJogadorNome();
                    this.equipaNome = join.getNomeEquipa();

                    GameState sala = Servidor.getSala(pinSala);
                    if (sala == null) {
                        out.println(JoinResponse.error("Sala inexistente!").serialize());
                        continue;
                    }

                    synchronized (sala) {
                        // 1. Verificar se nome já existe Should be separeted method
                        //called by the run()
                        if (sala.existeJogador(nomeJogador)) {
                            out.println(JoinResponse.error("Nome já em uso!").serialize());
                            continue;
                        }

                        // 2. Verificar se equipa está cheia no contexto da sala (sem estado global)
                        Equipa equipaObj = sala.getEquipa(equipaNome);
                        if (equipaObj != null && equipaObj.estaCompleta()) {
                            out.println(JoinResponse.error("Equipa " + equipaNome + " está cheia! (2/2 jogadores)").serialize());
                            continue;
                        }

                        // 3. Instanciar o jogador do lado do servidor e registar na equipa da sala
                        Jogador novoJogador = new Jogador(nomeJogador);
                        boolean sucesso = sala.addEquipa(equipaNome, novoJogador);
                        
                        if (!sucesso) {
                            out.println(JoinResponse.error("Não foi possível adicionar à equipa!").serialize());
                            continue;
                        }

                        Servidor.registarCliente(pinSala, this);

                        out.println(JoinResponse.ok(nomeJogador + " entrou na sala " + pinSala + " da equipa " + equipaNome).serialize());
                        
                        // 5. Enviar ao cliente o estado atual da equipa através do novo protocolo
                        Equipa equipaAtualizada = sala.getEquipa(equipaNome);
                        if (equipaAtualizada != null) {
                            TeamStatusResponse estado = equipaAtualizada.estaCompleta()
                                ? TeamStatusResponse.completa(equipaNome, equipaAtualizada.getNumeroJogadores())
                                : TeamStatusResponse.incompleta(equipaNome, equipaAtualizada.getNumeroJogadores());

                            System.out.println(nomeJogador + " juntou-se à equipa " + equipaNome + " -> " + estado.serialize());
                            // Notificamos todos os clientes da mesma sala para que o estado fique visível sem novo clique
                            Servidor.notificarTodosClientes(pinSala, estado.serialize());
                        }

                        if (sala.canStart()) {
                            System.out.println("Jogo pode iniciar!");
                            Servidor.notificarTodosClientes(pinSala, "JOGO_INICIAR");
                        }
                    }

                    continue;
                }
                else if (CheckSalaRequest.matches(msg)) {
                    // Pedido simples para validar o PIN de sala antes de efetuar JOIN
                    CheckSalaRequest checkSala;
                    try {
                        checkSala = CheckSalaRequest.fromRaw(msg);
                    } catch (IllegalArgumentException e) {
                        out.println(CheckSalaResponse.error("Formato inválido! Usa: CHECK_SALA <PIN>").serialize());
                        continue;
                    }

                    GameState sala = Servidor.getSala(checkSala.getPin());
                    if (sala != null) {
                        out.println(CheckSalaResponse.ok("").serialize());
                    } else {
                        out.println(CheckSalaResponse.error("Sala não existe").serialize());
                    }
                }
                else if (TeamStatusRequest.matches(msg)) {
                    // CHECK_EQUIPA agora traduzido por TeamStatusRequest/Response
                    TeamStatusRequest pedido;
                    try {
                        pedido = TeamStatusRequest.fromRaw(msg);
                    } catch (IllegalArgumentException e) {
                        out.println(TeamStatusResponse.incompleta("?", 0).serialize());
                        continue;
                    }

                    GameState sala = Servidor.getSala(pedido.getPinSala());
                    if (sala == null) {
                        out.println("EQUIPA_ERROR Sala não existe");
                        continue;
                    }

                    Equipa equipa = sala.getEquipa(pedido.getEquipaNome());
                    if (equipa == null) {
                        out.println(TeamStatusResponse.incompleta(pedido.getEquipaNome(), 0).serialize());
                        continue;
                    }

                    TeamStatusResponse resposta = equipa.estaCompleta()
                            ? TeamStatusResponse.completa(equipa.getNome(), equipa.getNumeroJogadores())
                            : TeamStatusResponse.incompleta(equipa.getNome(), equipa.getNumeroJogadores());
                    out.println(resposta.serialize());
                }
                else if (msg.startsWith("ESTADO_EQUIPAS")) {
                    GameState sala = pinSala != null ? Servidor.getSala(pinSala) : null;
                    if (sala == null) {
                        out.println("ESTADO_ERRO Sala desconhecida");
                        continue;
                    }
                    StringBuilder estado = new StringBuilder();
                    for (Equipa equipa : sala.listarEquipas()) {
                        estado.append(equipa.getStatusEquipa()).append(" | ");
                    }
                    out.println("ESTADO " + estado.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
