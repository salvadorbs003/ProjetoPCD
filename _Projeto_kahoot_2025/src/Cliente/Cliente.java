package Cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

    public Cliente(String host, int port, String pin, String equipa, String nome) {
        this.host = host;
        this.port = port;
        this.pin = pin;
        this.equipa = equipa;
        this.nome = nome;
    }

    public void ligar() {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println(" Ligado ao servidor em " + host + ":" + port);
            out.println("JOIN " + pin + " " + equipa + " " + nome);

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
            out.println("JOIN " + pin + " " + equipa + " " + nome);

            String resposta;
            while ((resposta = in.readLine()) != null) {
                System.out.println("Servidor: " + resposta);

                if (resposta.startsWith("JOIN_OK")) {
                    return true; // sucesso
                } else if (resposta.startsWith("JOIN_ERROR")) {
                    return false; // erro no PIN ou formato
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

            // primeira linha é sempre a mensagem de boas-vindas
            String bemVindo = in.readLine();
            System.out.println("Servidor: " + bemVindo);

            // envia o comando para verificar a sala
            out.println("CHECK_SALA " + pin);

            // lê a resposta real à verificação
            String resposta = in.readLine();
            System.out.println("Servidor: " + resposta);

            return resposta != null && resposta.equals("SALA_OK");

        } catch (IOException e) {
            System.err.println("❌ Erro ao validar sala: " + e.getMessage());
            return false;
        }
    }
    public String verificarEstadoEquipa(String nomeEquipa) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Envia comando para verificar equipa
            out.println("CHECK_EQUIPA " + pin + " " + nomeEquipa);

            String resposta;
            while ((resposta = in.readLine()) != null) {
                System.out.println("Servidor: " + resposta);
                
                if (resposta.startsWith("EQUIPA_COMPLETA")) {
                    return "COMPLETA";
                } else if (resposta.startsWith("EQUIPA_INCOMPLETA")) {
                    return "INCOMPLETA";
                }
            }
            return "INCOMPLETA";

        } catch (IOException e) {
            System.err.println("❌ Erro ao verificar equipa: " + e.getMessage());
            return "INCOMPLETA";
        }
    }

}