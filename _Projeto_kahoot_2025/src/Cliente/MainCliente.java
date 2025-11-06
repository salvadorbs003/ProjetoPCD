package Cliente;

import java.util.ArrayList;
import java.util.List;

import GUI.Codigo_PINInicial_Frame;

public class MainCliente {
	
	
	public static void main(String[] args) {
		
		if (args.length == 5) {
            String host = args[0];
            int port = Integer.parseInt(args[1]);
            String pin = args[2];
            String equipa = args[3];
            String nome = args[4];

            System.out.printf("A ligar a %s:%d | Sala: %s | Equipa: %s | Jogador: %s%n",
                    host, port, pin, equipa, nome);

            Cliente cliente = new Cliente(host, port, pin, equipa, nome);
            cliente.ligar();
            return;
        }
     
        new Codigo_PINInicial_Frame();

    }

    /*
    private int perguntaAtual = 0;
    private List<Pergunta> perguntas;
    private Pergunta pergunta; 
    
    

    public MainCliente() {
    	Lista_Jogadores.adicionarJogador(new Jogador("Laura"));
        Lista_Jogadores.adicionarJogador(new Jogador("Pedro"));
        Lista_Jogadores.adicionarJogador(new Jogador("Maria"));
        Lista_Jogadores.adicionarJogador(new Jogador("Salvador"));
        
        perguntas = QuizLoader.load("src/z_kahoot/lista_perguntas.json");
        if (perguntas == null || perguntas.isEmpty()) {
            System.err.println("❌ Erro: não foram encontradas perguntas no ficheiro JSON!");
            return;
        }

        // Adiciona todas à lista global
        for (int i = 0; i < perguntas.size(); i++) {
            perguntas.get(i).setId(i + 1); // se quiseres numerar
            Lista_Perguntas.adicionarPergunta(perguntas.get(i));
        }


            mostrarTelaPIN();
    }
   
    private void mostrarTelaPIN() {
        new Codigo_PINInicial_Frame(); // quando termina, chama mostrarTelaNomes()
    }

    // Tela dos nomes
    public void mostrarTelaNomes() {
        new Nomes_EntrarJogo_Frame(); // quando termina, chama mostrarTelaEntrada()
    }

    // Contagem para começar o jogo
    public void mostrarTelaEntrada() {
        new Entrada_Jogo_Frame(); // no fim da contagem, chama mostrarPergunta()
    }

    public void iniciarJogo() {
        mostrarPergunta();
    }

    // Mostra a pergunta com base no índice atual
    public void mostrarPergunta() {
        if (perguntaAtual < perguntas.size()) {
             pergunta = perguntas.get(perguntaAtual);
            new Perguntas_Frame(pergunta, perguntaAtual + 1);
        } else {
            mostrarClassificacaoFinal();
        }
    }
    
    public void mostrarPontuacoes() {
    	if (pergunta != null) { 
            new Pontuacoes_Frame(0, pergunta.getId());
        } else {
            System.out.println("Erro: nenhuma pergunta ativa!");
        }    }
    
    public void proximaPergunta() {
        perguntaAtual++;
        mostrarPergunta();
    }

    private void mostrarClassificacaoFinal() {
        new Classificacao_Final_Frame(0, "Quiz de O Estranho Mundo de Jack");
    }
    
    
    public static void main(String[] args) {
        new MainCliente();
    }
    */
	
}