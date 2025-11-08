package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Protocolos.CheckSalaRequest;
import Protocolos.CheckSalaResponse;
import Protocolos.JoinRequest;
import Protocolos.JoinResponse;
import Protocolos.TeamStatusRequest;
import Protocolos.TeamStatusResponse;

//Vcs vão ter de manter a socket aberta para poderem ler pedidos por parte do 
//cliente e poder enviar um pedido de espera para não haver o kick start 
//automático da pagina de contagem decrescente como está at thee moment
//ver comenário aqui Nomes_EntrarJogo_Frame 

public class Cliente {
	private String host;
    private int port;
    private String pin;
    private String equipa;
    private String nome;
    private final JoinRequest join;

    public Cliente(String host, int port, String pin, String equipa, String nome) {
        this.host = host;
        this.port = port;
        this.pin = pin;
        this.equipa = equipa;
        this.nome = nome;
        join = new JoinRequest(pin, nome, equipa);
    }

    public void ligar() {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println(" Ligado ao servidor em " + host + ":" + port);
            out.println(join.serialize());

            String resposta;
            while ((resposta = in.readLine()) != null) {
                System.out.println("Servidor: " + resposta);
            }

        } catch (IOException e) {
            System.err.println(" Erro ao ligar ao servidor: " + e.getMessage());
        }
    }
    
    public boolean ligarComRetorno() {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Envia o pedido JOIN
            out.println(join.serialize());
            
            String resposta;
            while ((resposta = in.readLine()) != null) {
                System.out.println("Servidor: " + resposta);
                if(JoinResponse.matches(resposta)){
                    JoinResponse answer = JoinResponse.formJoin(resposta);
                    if (answer.isOk()) {
                           System.out.println("Ligado com sucesso!");
                    }  else {
                    return false; // erro no PIN ou formato
                    }

                }
                else if (resposta.startsWith("ESTADO_EQUIPA INCOMPLETA")) {
                    System.out.println("Equipa incompleta - à espera do 2º jogador");
                }
                else if (resposta.startsWith("ESTADO_EQUIPA COMPLETA")) {
                    System.out.println("Equipa completa! 2/2 jogadores");
                }
                else if (resposta.startsWith("JOGO_INICIAR")) {
                    System.out.println("Todas as equipas prontas! A iniciar jogo...");
                    return true; // sucesso
                }
            }

            return false; // se nunca receber resposta

        } catch (IOException e) {
            System.err.println(" Erro ao ligar ao servidor: " + e.getMessage());
            return false;
        }
    }

    // public boolean startGame(){
    //     try (Socket socket = new Socket(host, port);
    //          BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    //          PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

    //         if(ligarComRetorno()){

    //         }
    //     }catch (Exception e) {
    //     // TODO: handle exception
    //     }
            
    // }
    
    public boolean validarSala() {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            CheckSalaRequest check = new CheckSalaRequest(pin);
            // primeira linha é sempre a mensagem de boas-vindas
            String bemVindo = in.readLine();
            System.out.println("Servidor: " + bemVindo);

            // envia o comando para verificar a sala
            out.println(check.serialize());

            // lê a resposta real à verificação
            String resposta = in.readLine();
            System.out.println("Servidor: " + resposta);
            CheckSalaResponse answer = CheckSalaResponse.fromRaw(resposta); 

            return answer.isOk();

        } catch (IOException e) {
            System.err.println("❌ Erro ao validar sala: " + e.getMessage());
            return false;
        }
    }
    public String verificarEstadoEquipa(String nomeEquipa) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            TeamStatusRequest status = new TeamStatusRequest(pin, nomeEquipa);

            // Envia comando para verificar equipa usando o protocolo tipado
            out.println(status.serialize());

            String resposta;
            while ((resposta = in.readLine()) != null) {
                System.out.println("Servidor: " + resposta);
                if (TeamStatusResponse.matches(resposta)){
                    TeamStatusResponse estado = TeamStatusResponse.fromRaw(resposta);
                    if (estado.isCompleta()) {
                        return "COMPLETA";
                    } else {
                        return "INCOMPLETA";
                    }
                    
                }

            }
            return "INCOMPLETA";

        } catch (IOException e) {
            System.err.println("❌ Erro ao verificar equipa: " + e.getMessage());
            return "INCOMPLETA";
        }
    }

}
