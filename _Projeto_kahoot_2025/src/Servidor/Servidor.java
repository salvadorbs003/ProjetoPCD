package Servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
import Protocolos.ErrorResponse;
import Protocolos.GameFinished;
import Protocolos.GameStartRequest;
import Protocolos.JoinRequest;
import Protocolos.JoinResponse;
import Protocolos.Mensagem;
import Protocolos.MensagemChat;
import Protocolos.NextQuestion;
import Protocolos.NextQuestionRequest;
import Protocolos.StartNotification;
import Protocolos.SubmitAnswerRequest;
import Protocolos.TeamStatusRequest;
import Protocolos.TeamStatusResponse;
import Quizz.Pergunta;
import Quizz.Quiz;

public class Servidor {

    private static final int PORT = 12345;
    private static final Map<String, GameState> salas = new HashMap<>();
    private static final Random random = new Random();
    private static final Map<String, List<ClientHandler>> clientesPorSala = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor ativo na porta " + PORT);

            // Admin Command Thread
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

            // Client Accept Loop
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nova conexão: " + socket.getInetAddress());
                new Thread(new ClientHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- GAME ROOM MANAGEMENT ---

    private static synchronized void criarSala() {
        String pin = gerarPIN();
        Quiz quizz = QuizLoader.load(0);
        if (quizz == null || quizz.getPerguntas() == null || quizz.getPerguntas().isEmpty()) {
            System.err.println("Falha ao criar sala: quiz não disponível.");
            return;
        }
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
        if (clientes == null || clientes.isEmpty())
            return;

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
        System.out.println("Mensagem " + mensagem.getClass().getSimpleName() + " enviada para " + enviados
                + " clientes na sala " + pin);
    }

    // --- PROTOCOL PROCESSING ---

    public static void processMsg(ClientHandler handler, Mensagem msg) {
        try {
            Mensagem response = null;

            if (msg instanceof JoinRequest req) {
                response = processarJoin(handler, req);
            } else if (msg instanceof CheckSalaRequest req) {
                response = processarCheckSala(req);
            } else if (msg instanceof TeamStatusRequest req) {
                response = processarTeamStatus(req);
            } else if (msg instanceof MensagemChat chat) {
                notificarTodosClientes(handler.getPinSala(), chat);
                return; // Chats are broadcast only
            } else if (msg instanceof CheckPlayerRequest req) {
                response = processarCheckPlayer(req);
            } else if (msg instanceof GameStartRequest req) {
                response = processarGameStart(handler, req);
            } else if (msg instanceof SubmitAnswerRequest req) {
                response = processarResposta(req);
            } else if (msg instanceof NextQuestionRequest req) {
                response = processarNextQuestion(req);
            } else {
                response = new ErrorResponse("Mensagem não reconhecida");
            }

            // Centralized send
            if (response != null) {
                handler.enviarMensagem(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            handler.enviarMensagem(new ErrorResponse("Erro interno do servidor"));
        }
    }

    // --- HANDLER METHODS ---

    public static Mensagem processarJoin(ClientHandler handler, JoinRequest join) {
        GameState sala = getSala(join.getPinSala());
        if (sala == null)
            return JoinResponse.error("Sala inexistente!");

        synchronized (sala) {
            String nome = join.getJogadorNome();
            String equipa = join.getNomeEquipa();

            if (nome == null || nome.trim().isEmpty())
                return JoinResponse.error("Nome inválido!");
            if (equipa == null || equipa.trim().isEmpty())
                return JoinResponse.error("Equipa inválida!");

            if (sala.existeJogador(nome))
                return JoinResponse.error("Nome já existe!");

            Equipa team = sala.getEquipa(equipa);
            if (team != null && team.estaCompleta())
                return JoinResponse.error("Equipa cheia!");

            Jogador novoJogador = new Jogador(nome.trim(), equipa.trim());
            if (!sala.addEquipa(equipa.trim(), novoJogador)) {
                return JoinResponse.error("Erro ao adicionar equipa.");
            }

            // Update Handler Context
            handler.setContext(join.getPinSala(), nome.trim(), equipa.trim());
            registarCliente(join.getPinSala(), handler);

            // --- FIX STARTS HERE ---
            // 1. Send the JoinResponse success IMMEDIATELY (before broadcasting start)
            handler.enviarMensagem(JoinResponse.ok("Sucesso!"));

            // 2. Broadcast Updates
            Equipa equipaAtualizada = sala.getEquipa(equipa);
            TeamStatusResponse estadoEquipa = equipaAtualizada.estaCompleta()
                    ? TeamStatusResponse.completa(equipa, equipaAtualizada.getNumeroJogadores())
                    : TeamStatusResponse.incompleta(equipa, equipaAtualizada.getNumeroJogadores());

            notificarTodosClientes(join.getPinSala(), estadoEquipa);

            if (sala.canStart()) {
                StartNotification notify = new StartNotification(join.getPinSala(), 0);
                notificarTodosClientes(join.getPinSala(), notify);
            }

            // 3. Return null so processMsg doesn't send a response again
            return null;
        }
    }

    public static Mensagem processarCheckSala(CheckSalaRequest req) {
        GameState sala = getSala(req.getPin());
        return (sala != null)
                ? CheckSalaResponse.ok("Sala existe.")
                : CheckSalaResponse.error("Sala não existe.");
    }

    public static Mensagem processarTeamStatus(TeamStatusRequest req) {
        GameState game = getSala(req.getPinSala());
        if (game == null)
            return TeamStatusResponse.incompleta(req.getEquipaNome(), 0);

        synchronized (game) {
            Equipa team = game.getEquipa(req.getEquipaNome());
            if (team == null)
                return TeamStatusResponse.incompleta(req.getEquipaNome(), 0);

            return team.estaCompleta()
                    ? TeamStatusResponse.completa(req.getEquipaNome(), team.getNumeroJogadores())
                    : TeamStatusResponse.incompleta(req.getEquipaNome(), team.getNumeroJogadores());
        }
    }

    public static Mensagem processarCheckPlayer(CheckPlayerRequest req) {
        GameState sala = getSala(req.getPin());
        if (sala == null)
            return CheckPlayerResponse.error("Sala não existe");

        synchronized (sala) {
            return sala.existeJogador(req.getNamePlayer())
                    ? CheckPlayerResponse.error("Jogador existe")
                    : CheckPlayerResponse.ok("Jogador não existe");
        }
    }

    public static Mensagem processarGameStart(ClientHandler handler, GameStartRequest req) {
        GameState sala = getSala(req.getPinSala());
        if (sala == null)
            return new ErrorResponse("Sala não encontrada");

        synchronized (sala) {
            if (!sala.isRoundInitialized()) {
                System.out.println("Initializing round number " + sala.getRound());
                sala.setAvailableQ(0); // Pick random question
                sala.prepareNextRound(req.getTimeLimitSeconds());
            }

            if (sala.getCurrentPergunta() == null) {
                return new ErrorResponse("Erro: Jogo sem perguntas.");
            }

            return new NextQuestion(
                    req.getPinSala(),
                    sala.getRound(),
                    req.getTimeLimitSeconds(),
                    sala.getCurrentPergunta(),
                    sala.getScoreboard());
        }
    }

    public static Mensagem processarResposta(SubmitAnswerRequest req) {
        System.out.println(">>> [DEBUG] Server: processarResposta " + req.getNomeJogador());
        GameState sala = getSala(req.getPinSala());
        if (sala == null)
            return new ErrorResponse("Sala não encontrada");

        Pergunta currentP;
        Equipa equipa;
        Jogador jogador;
        HomeMade.GroupBarrier barrier = null;

        // 1. Initial Safe Check (Synchronized)
        synchronized (sala) {
            currentP = sala.getCurrentPergunta();

            // [FIX] If game ended or round timed out, reject answer gracefully to prevent
            // Crash
            if (currentP == null) {
                return new ErrorResponse("Ronda já terminou. 0 pontos.");
            }

            equipa = sala.getEquipa(req.getNomeEquipa());
            if (equipa == null)
                return new ErrorResponse("Equipa não encontrada");

            jogador = equipa.getJogadores().stream()
                    .filter(j -> j.getNome().equals(req.getNomeJogador()))
                    .findFirst().orElse(null);

            if (jogador == null)
                return new ErrorResponse("Jogador não encontrado");

            // Prepare barrier if in Group Mode
            if (currentP.getType() == Pergunta.Type.GROUP) {
                barrier = sala.getTeamBarriers().get(equipa.getNome());
            }
        }

        // 2. Barrier Wait (MUST BE OUTSIDE SYNCHRONIZED to prevent Deadlock)
        if (currentP.getType() == Pergunta.Type.GROUP && barrier != null) {
            System.out.println(">>> [DEBUG] Server: Waiting at barrier for team " + equipa.getNome());
            barrier.await(); // This blocks! We must not hold 'sala' lock here.
            System.out.println(">>> [DEBUG] Server: Barrier passed for " + req.getNomeJogador());
        }

        // 3. Scoring & Latch (Synchronized)
        synchronized (sala) {
            boolean isCorrect = (req.getAnswerIndex() == currentP.getCorrect());
            int pointsEarned = 0;

            if (currentP.getType() == Pergunta.Type.INDIVIDUAL) {
                HomeMade.IndiSem sem = sala.getCurrentIndiSem();
                if (sem != null) {
                    pointsEarned = sem.points(currentP.getPoints(), isCorrect);
                    jogador.aumentarPontuacao(pointsEarned);
                }
            } else {
                // Group Mode Scoring
                if (isCorrect) {
                    pointsEarned = currentP.getPoints();
                    jogador.aumentarPontuacao(pointsEarned);
                }
            }

            // Count for global round latch (signal that this player is done)
            if (sala.getCurrentRoundLatch() != null) {
                sala.getCurrentRoundLatch().countAnswer();
            }

            return new ErrorResponse("Resposta registada. Pontos: " + pointsEarned);
        }
    }

    public static Mensagem processarNextQuestion(NextQuestionRequest req) {
        System.out.println(">>> [DEBUG] Server: NextQuestion requested by " + req.getPinSala());
        GameState sala = getSala(req.getPinSala());
        if (sala == null)
            return new ErrorResponse("Sala fechada.");

        HomeMade.RoundLatch latch = null;
        boolean mustWait = false;

        // 1. Determine if we need to wait (Sync Check)
        synchronized (sala) {
            // [FIX] Only wait if the client is on the SAME round as the server.
            // If sala.getRound() > req.getCurrentRound(), it means the round
            // finished while this packet was in transit. Catch up immediately!
            if (sala.getRound() == req.getCurrentRound()) {
                mustWait = true;
                latch = sala.getCurrentRoundLatch();
            }
        }

        // 2. Blocking Wait (Outside synchronized block to allow others to answer)
        if (mustWait && latch != null) {
            latch.waitForTimeout();
        }

        // 3. Prepare Next Round / Check Game End
        synchronized (sala) {
            // Only the first thread to wake up advances the round state
            if (sala.getRound() == req.getCurrentRound()) {
                System.out.println(">>> [DEBUG] Server: Preparing Next Round...");
                sala.setAvailableQ(0); // This might set currentPergunta to null if empty

                // If questions ran out, we do NOT prepare next round.
                if (sala.getCurrentPergunta() != null) {
                    sala.prepareNextRound(45);
                }
            }

            // 4. Check Game Over [FIX for Client Crash]
            // If currentPergunta is null, the game is over.
            // Send GameFinished to PREVENT Client NullPointerException.
            if (sala.getCurrentPergunta() == null) {
                System.out.println(">>> [DEBUG] Server: Sending GameFinished to client.");
                return new GameFinished(sala.getScoreboard());
            }

            return new NextQuestion(
                    sala.getCodigoPIN(),
                    sala.getRound(),
                    45, // Time limit
                    sala.getCurrentPergunta(),
                    sala.getScoreboard());
        }
    }
}