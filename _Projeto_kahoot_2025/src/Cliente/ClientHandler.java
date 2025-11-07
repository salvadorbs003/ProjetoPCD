package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import GameState.Equipa;
import GameState.Jogador;
import Servidor.GameState;
import Servidor.Servidor;

public class ClientHandler implements Runnable {
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String pinSala;
    private String nomeJogador;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    public void enviarMensagem(String mensagem) {
        if (out != null) {
            out.println(mensagem);
        }
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Bem-vindo ao IsKahoot!");
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("Mensagem do cliente: " + msg);
                
                if (msg.startsWith("JOIN")) {
                    String[] partes = msg.split(" ");
                    if (partes.length == 4) {
                        String pin = partes[1];
                        String equipa = partes[2];
                        String nome = partes[3];
                        
                        this.pinSala = pin;
                        this.nomeJogador = nome;

                        GameState sala = Servidor.getSala(pin);
                        if (sala == null) {
                            out.println("JOIN_ERROR Sala inexistente!");
                            continue;
                        }

                        synchronized (sala) {
                            // 1. Verificar se nome já existe
                            if (sala.existeJogador(nome)) {
                                out.println("JOIN_ERROR Nome já em uso!");
                                continue;
                            }

                            // 2. Verificar se equipa está cheia
                            Equipa equipaObj = Equipa.buscarEquipa(equipa);
                            if (equipaObj != null && equipaObj.estaCompleta()) {
                                out.println("JOIN_ERROR Equipa " + equipa + " está cheia! (2/2 jogadores)");
                                continue;
                            }

                            // 3. Adicionar jogador à equipa
                            Jogador novoJogador = new Jogador(nome);
                            boolean sucesso = Equipa.adicionarJogadorAEquipa(novoJogador, equipa);
                            
                            if (!sucesso) {
                                out.println("JOIN_ERROR Não foi possível adicionar à equipa!");
                                continue;
                            }

                            // 4. Adicionar também ao GameState (para compatibilidade)
                            sala.adicionarJogador(novoJogador);
                            
                            Servidor.registarCliente(pin, this);

                            out.println("JOIN_OK " + nome + " entrou na sala " + pin + " da equipa " + equipa);
                            
                            // 5. Mostrar estado no SERVIDOR
                            Equipa equipaAtualizada = Equipa.buscarEquipa(equipa);
                            System.out.println(nome + " juntou-se à equipa " + equipa);
                            
                            if (equipaAtualizada.estaIncompleta()) {
                                System.out.println("Equipa Incompleta - 1/2 jogadores");
                                out.println("ESTADO_EQUIPA INCOMPLETA"); 
                                
                            } else if (equipaAtualizada.estaCompleta()) {
                                System.out.println("Equipa " + equipa + " completa! 2/2 jogadores");
                                out.println("ESTADO_EQUIPA COMPLETA");
                            }
                            
                            // 6. Verificar se jogo pode iniciar (2 equipas completas)
                            if (Equipa.podeIniciarJogo()) {
                                System.out.println("Jogo pode iniciar!");
                                Servidor.notificarTodosClientes(pin, "JOGO_INICIAR");                            }
                        }

                    } else {
                        out.println("JOIN_ERROR Formato inválido! Usa: JOIN <PIN> <Equipa> <Nome>");
                    }
                }
                else if (msg.startsWith("CHECK_SALA")) {
                    String[] partes = msg.split(" ");
                    if (partes.length == 2) {
                        String pin = partes[1];
                        GameState sala = Servidor.getSala(pin);
                        if (sala != null) {
                            out.println("SALA_OK");
                        } else {
                            out.println("SALA_ERROR");
                        }
                    } else {
                        out.println("SALA_ERROR");
                    }
                }
                else if (msg.startsWith("CHECK_EQUIPA")) {
                    String[] partes = msg.split(" ");
                    if (partes.length == 3) {
                        String pin = partes[1];
                        String equipa = partes[2];
                        
                        GameState sala = Servidor.getSala(pin);
                        if (sala == null) {
                            out.println("EQUIPA_ERROR Sala não existe");
                            continue;
                        }
                        
                        // Verificar se equipa está completa
                        Equipa equipaObj = Equipa.buscarEquipa(equipa);
                        if (equipaObj != null) {
                            if (equipaObj.estaCompleta()) {
                                out.println("EQUIPA_COMPLETA");
                                System.out.println("Verificação: Equipa " + equipa + " COMPLETA (2/2)");
                            } else {
                                out.println("EQUIPA_INCOMPLETA");
                                System.out.println("Verificação: Equipa " + equipa + " INCOMPLETA (" + 
                                                 equipaObj.getNumeroJogadores() + "/2)");
                            }
                        } else {
                            out.println("EQUIPA_INCOMPLETA");
                            System.out.println("Verificação: Equipa " + equipa + " NÃO EXISTE (0/2)");
                        }
                    }
                }
                else if (msg.startsWith("ESTADO_EQUIPAS")) {
                    StringBuilder estado = new StringBuilder();
                    for (Equipa equipa : Equipa.getTodasEquipas()) {
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