package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Cliente.ClientHandler;
import GameState.QuizLoader;

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
        clientesPorSala.computeIfAbsent(pin, k -> new ArrayList<>()).add(cliente);
        System.out.println("Cliente registado na sala " + pin + ". Total: " + clientesPorSala.get(pin).size());
    }
    public static synchronized void notificarTodosClientes(String pin, String mensagem) {
        List<ClientHandler> clientes = clientesPorSala.get(pin);
        if (clientes != null) {
            for (ClientHandler cliente : clientes) {
                cliente.enviarMensagem(mensagem);
            }
            System.out.println("Mensagem '" + mensagem + "' enviada para " + clientes.size() + " clientes");
        }
    

    }
}
