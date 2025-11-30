package Cliente;

import java.io.EOFException;

/**
 * Camada cliente responsável por enviar/receber objetos de protocolo
 * (JoinRequest, CheckSalaRequest, TeamStatusRequest, GameStartNotification).
 * Cada método abre a socket necessária e trata o respetivo ciclo de pedidos.
 */


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import Protocolos.CheckPlayerRequest;
import Protocolos.CheckPlayerResponse;
import Protocolos.CheckSalaRequest;
import Protocolos.CheckSalaResponse;
import Protocolos.GameStartNotification;
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

    public Cliente(String host, int port, String pin, String equipa, String nome) {
        this.host = host;
        this.port = port;
        this.pin = pin;
        this.equipa = equipa;
        this.nome = nome;
    }

    /**
     * Mantém uma ligação longa: envia JoinRequest e escuta respostas
     * JoinResponse/TeamStatusResponse/GameStartNotification até haver
     * erro (false) ou sinal para arrancar o jogo (true).
     */
    public boolean ligar() {
    	
    	System.out.println("Im trying to connect to the server!");
        try(Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.flush();
            JoinRequest join = new JoinRequest(pin, nome, equipa);
            
            // Envia o pedido JOIN
            out.writeObject(join);
            out.flush();

            while (true) {
                Object answer = in.readObject();
                if(answer instanceof JoinResponse joinResp){
                    if(!joinResp.isOk()){
                        System.err.println("Join falhou: " + joinResp.getMsg());
                        return false;
                    }
                    System.out.println("Join ok: " + joinResp.getMsg()); // confirmação do servidor
                    
                } else if(answer instanceof TeamStatusResponse){
                    System.out.println("Estado equipa: " + ((TeamStatusResponse) answer).getEquipaNome());
                } else if(answer instanceof GameStartNotification){
                    return true;
                }
            }

        } catch (EOFException ignored) {
            return false;
        }catch (IOException | ClassNotFoundException e) {
            System.err.println(" Erro ao ligar ao servidor: " + e.getMessage());
            return false;
        } 
    }

    /**
     * Pré-validação: envia um CheckSalaRequest e espera apenas um CheckSalaResponse.
     */
    public boolean validarSala() {
    	
    	System.out.println("Im validating the room");
        try(Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.flush();
            
            CheckSalaRequest req = new CheckSalaRequest(pin);
            // primeira linha é sempre a mensagem de boas-vindas
            out.writeObject(req);
            out.flush();

            Object obj = in.readObject();
            if(obj instanceof CheckSalaResponse resp){
                return resp.isOk();
            }
            return false;
            

        } catch (EOFException e) {
            System.err.println("❌ Erro ao validar sala: " + e.getMessage());
            return false;
        }catch (IOException | ClassNotFoundException e) {
            System.err.println(" Erro ao ligar ao servidor: " + e.getMessage());
            return false;
        } 
    }

    /**
     * Pré-validação: envia TeamStatusRequest (com equipa + nome) e recebe um TeamStatusResponse.
     */
    public int verificarEstadoEquipa(String nomeEquipa) {
    	
    	System.out.println("Im validanting the teamStatus");
        try (Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            out.flush();

            TeamStatusRequest status = new TeamStatusRequest(pin, nomeEquipa, nome);
            out.writeObject(status);
            out.flush();

            System.out.println("Servidor: " + status);
            Object obj = in.readObject();
            if (obj instanceof TeamStatusResponse resp){
                return resp.getPlayerCount();
            }
            return -1;

        } catch (EOFException e) {
            System.err.println("❌ Erro ao validar sala: " + e.getMessage());
            return -1;
        }catch (IOException | ClassNotFoundException e) {
            System.err.println(" Erro ao ligar ao servidor: " + e.getMessage());
            return -1;
        } 
    }

    public boolean verificarJogador(String jogadorNome, String pin){
        try (Socket socket = new Socket(host, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){
            out.flush();

            CheckPlayerRequest exists = new CheckPlayerRequest(jogadorNome, pin);
            out.writeObject(exists);
            out.flush();

            System.out.println("Servidor: " + exists);
            Object obj = in.readObject();
            if(obj instanceof CheckPlayerResponse resp){
                System.out.println(resp.isOk());
                return resp.isOk();
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}