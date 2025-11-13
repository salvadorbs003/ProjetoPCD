package Cliente;

/**
 * Handler do lado servidor para cada cliente conectado.
 * Apenas gere sockets/streams, delegando a lógica para Servidor.processMsg.
 */

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

import Servidor.Servidor;

public class ClientHandler implements Runnable {
    
    private final Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String pinSala;
    private String nomeJogador;
    private String equipaNome;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    public void run(){
        try {
            initializeStreams();
            Object obj;
            while(true){
                obj = in.readObject();
                Servidor.processMsg(this, obj); // entrega do objeto de protocolo ao servidor
            }
        }  catch (EOFException ignored) {
            // client closed connection
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro no handler: " + e.getMessage());
        } finally {
            closeStreams();
        }
    }
    
    private void initializeStreams() throws IOException{
        out  = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Envia qualquer objeto de protocolo serializável de volta ao cliente.
     */
    public void enviarMensagem(Serializable mensagem) {
        if (out == null) {
           return;
        }
        try {
            synchronized(out){ //avoids unexpected interactions with other handler state
                out.writeObject(mensagem);
                out.flush();
            }
        } catch (IOException e) {
            System.err.println("Falha ao enviar mensagem para cliente: " + e.getMessage());
        }
    }

    private void closeStreams(){
        if(out != null){
            try {
                out.flush();
                out.close();
                
            } catch (IOException ignored) {}
        }
        if(in != null){
            try {
                in.close();
            } catch (IOException ignored) {}
        }

        if(socket != null && !socket.isClosed()){
            try {
                socket.close();
            } catch (IOException ignored) {}
        }
    }

//Getters & Setters
    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getIn() {
        return in;
    }

    public ObjectOutputStream getOut() {
        return out;
    }

    public String getPinSala() {
        return pinSala;
    }

    public String getNomeJogador() {
        return nomeJogador;
    }

    public String getEquipaNome() {
        return equipaNome;
    }

    /**
     * Atualiza o contexto deste handler para o servidor poder associar pin/equipa/jogador.
     */
    public void setContext (String pinSala, String nomeJogador, String equipaNome){
        this.pinSala = pinSala;
        this.nomeJogador = nomeJogador;
        this.equipaNome = equipaNome;
    }
}
