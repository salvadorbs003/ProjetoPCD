package Servidor;

/**
 * Servidor central: cria salas, aceita sockets e processa todos os pedidos
 * do protocolo enviando respostas/broadcasts serializados.
 */

import GameState.Equipa;
import GameState.Jogador;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import Cliente.ClientHandler;
import GameState.QuizLoader;
import Protocolos.CheckSalaRequest;
import Protocolos.GameStartNotification;
import Protocolos.JoinRequest;
import Protocolos.JoinResponse;
import Protocolos.TeamStatusRequest;
import Protocolos.TeamStatusResponse;

import java.util.*;

public class Servidor {
	
	private static final int PORT = 12345;
    private static final Map<String, GameState> salas = new HashMap<>();
    private static final Random random = new Random();
    private static final Map<String, List<ClientHandler>> clientesPorSala = new HashMap<>();
    
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor ativo na porta " + PORT);
            
            
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
                            System.exit(0);
                        }
                        default -> System.out.println("Comando inválido!");
                    }
                }
            }).start();
            

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static synchronized void criarSala() {
        String pin = gerarPIN();
        GameState novaSala = new GameState(pin, QuizLoader.load("src/lista_perguntas.json"));
        salas.put(pin, novaSala);
        System.out.println(" Nova sala criada com PIN: " + pin);
    }
    
    private static String gerarPIN() {
        String pin;
        do {
            pin = String.format("%06d", random.nextInt(1_000_000)); // 000000–999999
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
        System.out.println(" Sala " + pin + " encerrada.");
    }
    
    public static synchronized void registarCliente(String pin, ClientHandler cliente) {
    	 System.out.println("Im in ClientesPorSala");
        clientesPorSala.computeIfAbsent(pin, k -> new ArrayList<>()).add(cliente);
        System.out.println("Cliente registado na sala " + pin + ". Total: " + clientesPorSala.get(pin).size());
    }

    public static synchronized void notificarTodosClientes(String pin, Serializable mensagem) {
        List<ClientHandler> clientes = clientesPorSala.get(pin);
        if (clientes == null || clientes.isEmpty()) {
            return;
        }

        Iterator<ClientHandler> it = clientes.iterator();
        while (it.hasNext()) {
            ClientHandler cliente = it.next();
            try {
                cliente.enviarMensagem(mensagem);
            } catch (Exception e) {
                it.remove(); // drop dead handlers
            }
        }
        System.out.println("Mensagem " + mensagem.getClass().getSimpleName()
            + " enviada para " + clientes.size() + " clientes");
    }


    /**
     * Dispatcher principal: associa cada objeto de protocolo ao respetivo handler.
     */
    public static void processMsg(ClientHandler handler, Object obj){
        if (obj instanceof JoinRequest join) {
            processarJoin(handler, join);
        } else if (obj instanceof CheckSalaRequest check) {
            handler.enviarMensagem(processarCheckSala(check));
        } else if (obj instanceof TeamStatusRequest req) {
            handler.enviarMensagem(processarTeamStatus(req));
        } else if (obj instanceof LobbyStateRequest lobbyReq) {
            handler.enviarMensagem(processarLobbyState(lobbyReq));
        } else {
            System.err.println("Mensagem desconhecida: " + obj);
        }
    }

    /**
     * Trata JoinRequest validando sala/equipa/nome e emitindo as respostas/broadcasts necessários.
     */
    public static void processarJoin(ClientHandler handler, JoinRequest join){
        String pin = join.getPinSala();
        String nome = join.getJogadorNome();
        String equipa = join.getNomeEquipa();
        Jogador j = new Jogador(nome, equipa);
        GameState sala = getSala(pin);
        if(sala == null){
            handler.enviarMensagem(JoinResponse.error("Sala inexistente!"));
            return;
        }
        synchronized(sala){
            Equipa team = sala.getEquipa(equipa);
            if(team != null && team.existsPlayer(nome)){
                handler.enviarMensagem(JoinResponse.error("Já existe um jogador: " + nome + " na esquipa escolha outro nome :)"));
            } else if(team != null && team.estaCompleta()){
                handler.enviarMensagem(JoinResponse.error("Equipa completa"));
            }else if(!sala.addEquipa(equipa, j)){
                handler.enviarMensagem(JoinResponse.error("Não foi possivel adicionar a equipa"));
            }else{
                Equipa equipaAtualizada = sala.getEquipa(equipa);
                handler.setContext(pin, nome, equipa);
                registarCliente(pin , handler);
                TeamStatusResponse estado = equipaAtualizada.estaCompleta()
                    ? TeamStatusResponse.completa(equipa, equipaAtualizada.getNumeroJogadores())
                    : TeamStatusResponse.incompleta(equipa, equipaAtualizada.getNumeroJogadores());
                     notificarTodosClientes(join.getPinSala(), estado);
                    if(sala.canStart()){
                        GameStartNotification notify = new GameStartNotification(pin);
                        notificarTodosClientes(join.getPinSala(), notify);
                    } 
                handler.enviarMensagem(JoinResponse.ok("O jogador com o nome: " + nome + " inscreveu-se com sucesso na equipa " + equipa));
            }
        } 
    }
}