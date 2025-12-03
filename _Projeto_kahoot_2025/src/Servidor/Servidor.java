package Servidor;


import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import Cliente.ClientHandler;
import GameState.Equipa;
import GameState.Jogador;
import GameState.QuizLoader;
import Protocolos.CheckPlayerRequest;
import Protocolos.CheckPlayerResponse;
import Protocolos.CheckSalaRequest;
import Protocolos.CheckSalaResponse;
import Protocolos.GameStartRequest;
import Protocolos.JoinRequest;
import Protocolos.JoinResponse;
import Protocolos.LobbyStateRequest;
import Protocolos.LobbyStateResponse;
import Protocolos.Mensagem;
import Protocolos.TeamStatusRequest;
import Protocolos.TeamStatusResponse;
import Quizz.Quiz;
import Protocolos.MensagemChat;
import Protocolos.NextQuestion;


public class Servidor {
    
    private static final int PORT = 12345;
    private static final Map<String, GameState> salas = new HashMap<>();
    private static final Random random = new Random();
    private static final Map<String, List<ClientHandler>> clientesPorSala = new HashMap<>();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor ativo na porta " + PORT);
            
            // Thread para comandos do admin
            new Thread(() -> {
                Scanner sc = new Scanner(System.in);
                while (true) {
                    System.out.println("\nComando: (1) Criar sala | (2) Listar salas | (3) Sair");
                    String cmd = sc.nextLine();

                    switch (cmd) {
                        case "1" -> criarSala();
                        case "2" -> listarSalas();
                        case "3" -> {
                            System.out.println("Encerrando servidor...");
                            sc.close();
                            System.exit(0);
                        }
                        default -> System.out.println("Comando inválido!");
                    }
                }
            }).start();
            
            // Loop principal para aceitar clientes
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nova conexão: " + socket.getInetAddress());
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //Cria a sala, gera PIN e loads the QUizz
    private static synchronized void criarSala() {
        String pin = gerarPIN();
        Quiz quizz = QuizLoader.load(0);
        if (quizz == null || quizz.getPerguntas() == null || quizz.getPerguntas().isEmpty()) {
            System.err.println("Falha ao criar sala: quiz não disponível.");
            return;
        }
        System.out.println(quizz.getPerguntas());
        GameState novaSala = new GameState(pin, quizz.getPerguntas());
        salas.put(pin, novaSala);
        clientesPorSala.put(pin, new ArrayList<>());
        System.out.println("Nova sala criada com PIN: " + pin);
    }
    
    private static String gerarPIN() {
        String pin;
        do {
            pin = String.format("%06d", random.nextInt(1_000_000));
        } while (salas.containsKey(pin));
        return pin;
    }
    
    private static void listarSalas() {
        if (salas.isEmpty()) {
            System.out.println("Nenhuma sala ativa.");
        } else {
            System.out.println("\n Salas ativas:");
            for (String pin : salas.keySet()) {
                System.out.println("→ Sala " + pin);
            }
      
        }
    }
    
    public static synchronized GameState getSala(String pin) {
        return salas.get(pin);
    }
    
    public static synchronized void removerSala(String pin) {
        salas.remove(pin);
        clientesPorSala.remove(pin);
        System.out.println("Sala " + pin + " encerrada.");
    }
    
    public static synchronized void registarCliente(String pin, ClientHandler cliente) {
        clientesPorSala.computeIfAbsent(pin, k -> new ArrayList<>()).add(cliente);
        System.out.println("Cliente registado na sala " + pin + ". Total: " + clientesPorSala.get(pin).size());
    }
    
    public static synchronized void removerCliente(String pin, ClientHandler cliente) {
        List<ClientHandler> clientes = clientesPorSala.get(pin);
        if (clientes != null) {
            clientes.remove(cliente);
            System.out.println("Cliente removido da sala " + pin + ". Restantes: " + clientes.size());
            if (clientes.isEmpty()) {
                removerSala(pin);
            }
        }
    }
    
    public static synchronized void notificarTodosClientes(String pin, Mensagem mensagem) {
        List<ClientHandler> clientes = clientesPorSala.get(pin);
        if (clientes == null || clientes.isEmpty()) {
            return;
        }
        Iterator<ClientHandler> it = clientes.iterator();
        int enviados = 0;
        while (it.hasNext()) {
            ClientHandler cliente = it.next();
            try {
                cliente.enviarMensagem(mensagem);
                enviados++;
            } catch (Exception e) {
                System.err.println("Erro a notificar cliente: " + e.getMessage());
                it.remove();
            }
        }
        System.out.println("Mensagem " + mensagem.getClass().getSimpleName()
            + " enviada para " + enviados + " clientes na sala " + pin);
    }
    
    public static void processMsg(ClientHandler handler, Mensagem msg) {
        try {
            if (msg instanceof JoinRequest req) {
                processarJoin(handler, req);

            } else if (msg instanceof CheckSalaRequest req) {
                handler.enviarMensagem(processarCheckSala(req));

            } else if (msg instanceof TeamStatusRequest req) {
                handler.enviarMensagem(processarTeamStatus(req));

            } else if (msg instanceof MensagemChat chat) {
                notificarTodosClientes(handler.getPinSala(), chat);

            } else if (msg instanceof CheckPlayerRequest req) {
                handler.enviarMensagem(processarCheckPlayer(req));

            } else {
                handler.enviarMensagem(new ErrorResponse("Mensagem não reconhecida"));
            }

        } catch (Exception e) {
            handler.enviarMensagem(new ErrorResponse("Erro interno do servidor"));
        }
    }

    
    public static CheckSalaResponse processarCheckSala(CheckSalaRequest request) {
    	 System.out.println("Im on processarCheckSala method!");
        GameState sala = getSala(request.getPin());
        if (sala != null) {
            return CheckSalaResponse.ok("Sala " + request.getPin() + " existe e está ativa.");
        } else {
            return CheckSalaResponse.error("Sala " + request.getPin() + " não existe.");
        }
    }
    
    public static void processarJoin(ClientHandler handler, JoinRequest join) {
        int round = 0;
        System.out.println("Im on processarJoin method!");

        GameState sala = getSala(join.getPinSala());
        if (sala == null) {
            handler.enviarMensagem(JoinResponse.error("Sala inexistente!"));
            return;
        }
        synchronized (sala) {
            String nome = join.getJogadorNome();
            String equipa = join.getNomeEquipa();
            
            if (nome == null || nome.trim().isEmpty()) {
                handler.enviarMensagem(JoinResponse.error("Nome do jogador inválido!"));
                return;
            }
            if (equipa == null || equipa.trim().isEmpty()) {
                handler.enviarMensagem(JoinResponse.error("Nome da equipa inválido!"));
                return;
            }
            
            if (sala.existeJogador(nome)) {
                handler.enviarMensagem(JoinResponse.error("Já existe um jogador com o nome '" + nome + "' na sala."));
                return;
            }
            
            Equipa team = sala.getEquipa(equipa);
            if (team != null && team.estaCompleta()) {
                handler.enviarMensagem(JoinResponse.error("Equipa '" + equipa + "' está completa."));
                return;
            }
            
            Jogador novoJogador = new Jogador(nome.trim(), equipa.trim());
            if (!sala.addEquipa(equipa.trim(), novoJogador)) {
                handler.enviarMensagem(JoinResponse.error("Não foi possível adicionar à equipa."));
                return;
            }
            
            handler.setContext(join.getPinSala(), nome.trim(), equipa.trim());
            registarCliente(join.getPinSala(), handler);
            
            Equipa equipaAtualizada = sala.getEquipa(equipa);
            TeamStatusResponse estadoEquipa = equipaAtualizada.estaCompleta()
                ? TeamStatusResponse.completa(equipa, equipaAtualizada.getNumeroJogadores())
                : TeamStatusResponse.incompleta(equipa, equipaAtualizada.getNumeroJogadores());
            
            notificarTodosClientes(join.getPinSala(), estadoEquipa);
            
            // if (sala.canStart()) {
            //     NextQuestion notify = new NextQuestion(join.getPinSala(), round, );
            //     notificarTodosClientes(join.getPinSala(), notify);
            // }
            
            handler.enviarMensagem(JoinResponse.ok("Jogador '" + nome + "' juntou-se à equipa '" + equipa + "' com sucesso!"));
        }
    }
    
    public static TeamStatusResponse processarTeamStatus(TeamStatusRequest req) {       
    	System.out.println("Im on processarTeamStatus method!");
    	
        GameState game = getSala(req.getPinSala());
        if (game == null) {
            return TeamStatusResponse.incompleta(req.getEquipaNome(), 0);
        }
        synchronized (game) {
            Equipa team = game.getEquipa(req.getEquipaNome());
            if (team == null) {
                return TeamStatusResponse.incompleta(req.getEquipaNome(), 0);
            }
            return team.estaCompleta()
                ? TeamStatusResponse.completa(req.getEquipaNome(), team.getNumeroJogadores())
                : TeamStatusResponse.incompleta(req.getEquipaNome(), team.getNumeroJogadores());
        }
    }
    
    public static LobbyStateResponse processarLobbyState(LobbyStateRequest lobbyReq, ClientHandler handler) {
        System.out.println("Im on processarLobbyState method!");

        String pin = handler.getPinSala();
        GameState sala = getSala(pin);
        if (sala == null) {
            return new LobbyStateResponse();
        }
        synchronized (sala) {
            return new LobbyStateResponse();
        }
    }
    
    public static CheckPlayerResponse processarCheckPlayer(CheckPlayerRequest req){
        System.out.println("Im on processarCheckPlayer method!");
         GameState sala = getSala(req.getPin());
        if (sala == null) {
            return CheckPlayerResponse.error("Sala não existe");
        }
        synchronized (sala) {
            return sala.existeJogador(req.getNamePlayer())
                ? CheckPlayerResponse.error("Jogador existe")
                : CheckPlayerResponse.ok("Jogador não existe");
        }
    }
    
    private static class ErrorResponse extends Mensagem {
        private static final long serialVersionUID = 1L;
        private final String error;
        public ErrorResponse(String error) { this.error = error; }
        public String getError() { return error; }
    }

}
