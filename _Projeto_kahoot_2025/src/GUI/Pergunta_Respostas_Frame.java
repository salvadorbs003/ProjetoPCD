package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import GameState.Lista_Jogadores;
import Perguntas.Lista_Perguntas;
import Quizz.Pergunta;

public class Pergunta_Respostas_Frame {

	private JFrame frame;
    private JLabel tempoLabel, perguntaLabel, indiceLabel;
    private JButton[] botoesResposta;
    private int tempoRestante;
    private boolean respondeu = false;
    private Thread temporizadorThread;
    private java.util.List<String> opcoes;
    private Pergunta pergunta;
    private int jogadorId;
    private JPanel painelPontuacoes;
    int perguntaAtualId;
    private int indicePergunta;




    public Pergunta_Respostas_Frame(int indicePergunta, Pergunta pergunta,  java.util.List<String> opcoes, int tempoRestante) {
    	this.pergunta = pergunta;
    	this.tempoRestante = tempoRestante;
    	this.opcoes = opcoes;
    	this.jogadorId = Lista_Jogadores.getIdJogadorAtual();
    	 this.indicePergunta = indicePergunta;
         this.perguntaAtualId = pergunta.getId();
    	
        frame = new JFrame("Kahoot - Pergunta " + indicePergunta);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(45, 25, 120));

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBackground(new Color(45, 25, 120));
        p1.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        indiceLabel = new JLabel("Pergunta " + indicePergunta);
        indiceLabel.setForeground(Color.WHITE);
        indiceLabel.setFont(new Font("Arial", Font.BOLD, 26));

        tempoLabel = new JLabel("⏱️" + tempoRestante + "s", SwingConstants.RIGHT);
        tempoLabel.setForeground(Color.WHITE);
        tempoLabel.setFont(new Font("Arial", Font.BOLD, 26));

        perguntaLabel = new JLabel(pergunta.getTexto(), SwingConstants.CENTER);
        perguntaLabel.setForeground(Color.WHITE);
        perguntaLabel.setFont(new Font("Arial", Font.BOLD, 28));
        perguntaLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        p1.add(indiceLabel, BorderLayout.WEST);
        p1.add(tempoLabel, BorderLayout.EAST);
        p1.add(perguntaLabel, BorderLayout.SOUTH);

        JPanel p2 = new JPanel(new GridLayout(2, 2, 20, 20));
        p2.setBackground(new Color(45, 25, 120));
        p2.setBorder(BorderFactory.createEmptyBorder(80, 100, 100, 200));

        Color[] cores = { new Color(220, 53, 69), new Color(0, 123, 255), new Color(255, 193, 7), new Color(40, 167, 69) };

        botoesResposta = new JButton[4];

        for (int i = 0; i < 4; i++) {
            botoesResposta[i] = new JButton(opcoes.get(i));
            botoesResposta[i].setFont(new Font("Arial", Font.BOLD, 20));
            botoesResposta[i].setForeground(Color.WHITE);
            botoesResposta[i].setBackground(cores[i]);
            botoesResposta[i].setFocusPainted(false);
            botoesResposta[i].setOpaque(true);
            botoesResposta[i].setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            p2.add(botoesResposta[i]);

            int idx = i;
            botoesResposta[i].addActionListener(e -> responder(idx));
        }

        frame.add(p1, BorderLayout.NORTH);
        frame.add(p2, BorderLayout.CENTER);
        frame.setVisible(true);
        
       PainelPontuacoes();


        iniciarContagem(); 
    }

    private void iniciarContagem() {
        temporizadorThread = new Thread(() -> {
            while (tempoRestante > 0 && !respondeu) {
                try {
                    Thread.sleep(1000); 
                } catch (InterruptedException e) {
                    return;
                }
                tempoRestante--; //assim passa de segundo a segundo
                tempoLabel.setText("⏱️" + tempoRestante + "s");
            }
            if (!respondeu) {
            	 respondeu = true; // evita duplicação
                 SwingUtilities.invokeLater(() -> {
                     JOptionPane.showMessageDialog(frame, "⏰ Tempo esgotado!", "Aviso", JOptionPane.WARNING_MESSAGE);
                     terminarRonda(false,-1);
                 });
                
            }
        });
        temporizadorThread.start();
    }

    private void responder(int idx) {
    	//se o jogador já respondeu uma vez, ele já sai da função porque não pode responder + que 1 vez
        if (respondeu == true) return;
        
        respondeu = true;

        //ainda se tem de ver a pontuação!!
        int pontos = tempoRestante * 10;
        
        boolean correta = (idx == pergunta.getIndiceCorreto());
        
        if (correta) {
            pontos = tempoRestante * 10;
            Lista_Jogadores.adicionarPontos(jogadorId, pontos);
            System.out.println("Resposta Correta! Jogador " + this.jogadorId + " + " + pontos + " pontos.");
        } else {
        	System.out.println("Resposta Errada! Jogador " + this.jogadorId + " + 0 pontos.");
        }
        atualizarPainelPontuacoes();
        terminarRonda(correta, idx);
    }

    private void terminarRonda(boolean respondeu, int respostaEscolhida) {
        respondeu = true;
        if (temporizadorThread != null && temporizadorThread.isAlive()) {
            temporizadorThread.interrupt();
        }
        
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    avancarParaProximaPergunta();
                    
                });
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void avancarParaProximaPergunta() {
        List<Pergunta> todasPerguntas = Lista_Perguntas.getPerguntas();
        
        if (todasPerguntas == null || todasPerguntas.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Erro: Nenhuma pergunta carregada!");
            return;
        }
        
      
        int proximoIndice = this.indicePergunta; // Índice atual
        Pergunta proximaPergunta = null;
        
        // Procurar próxima pergunta na lista
        for (int i = 0; i < todasPerguntas.size(); i++) {
            if (todasPerguntas.get(i).getId() == this.perguntaAtualId + 1) {
                proximaPergunta = todasPerguntas.get(i);
                proximoIndice = i + 1; // Índice para display
                break;
            }
        }
        
        if (proximaPergunta != null) {
            frame.dispose();
            new Pergunta_Respostas_Frame(
                proximoIndice,
                proximaPergunta,
                proximaPergunta.getOpcoes(),
                proximaPergunta.getTempo()
            );
        } else {

            frame.dispose();
            new Classificacao_Final_Frame(0, "Quiz Final");
        }
    }
    
    
    private void PainelPontuacoes() {
        painelPontuacoes = new JPanel();
        painelPontuacoes.setLayout(new BorderLayout());
        painelPontuacoes.setBackground(new Color(60, 40, 140));
        painelPontuacoes.setPreferredSize(new Dimension(300, 0));
        painelPontuacoes.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel titulo = new JLabel("🏆 PONTUAÇÕES", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        painelPontuacoes.add(titulo, BorderLayout.NORTH);
        
        // Conteúdo das pontuações (reutiliza a lógica do Pontuacoes_Frame)
        JPanel listaPontuacoes = criarListaPontuacoes();
        painelPontuacoes.add(listaPontuacoes, BorderLayout.CENTER);
        
        // Adicionar ao frame principal no lado DIREITO
        frame.add(painelPontuacoes, BorderLayout.EAST);
    }
    
    private JPanel criarListaPontuacoes() {
        String[] nomes = Lista_Jogadores.getNomes();
        int[] pontos = Lista_Jogadores.getPontuacoes();
        
        JPanel painel = new JPanel();
        painel.setLayout(new GridLayout(nomes.length, 1, 5, 5));
        painel.setBackground(new Color(60, 40, 140));
        
        for (int i = 0; i < nomes.length; i++) {
            JPanel linha = new JPanel(new BorderLayout());
            linha.setBackground(new Color(80, 60, 160));
            linha.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            
            JLabel nome = new JLabel((i + 1) + ". " + nomes[i]);
            nome.setFont(new Font("Arial", Font.BOLD, 14));
            nome.setForeground(Color.WHITE);
            
            JLabel pontosLabel = new JLabel(pontos[i] + " pts");
            pontosLabel.setFont(new Font("Arial", Font.BOLD, 14));
            pontosLabel.setForeground(Color.YELLOW);
            
            linha.add(nome, BorderLayout.WEST);
            linha.add(pontosLabel, BorderLayout.EAST);
            
            painel.add(linha);
        }
        
        return painel;
    }
    private void atualizarPainelPontuacoes() {
        painelPontuacoes.removeAll();
        
        // Título
        JLabel titulo = new JLabel("🏆 PONTUAÇÕES", SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        painelPontuacoes.add(titulo, BorderLayout.NORTH);
        
        // Conteúdo atualizado das pontuações
        JPanel listaPontuacoes = criarListaPontuacoes();
        painelPontuacoes.add(listaPontuacoes, BorderLayout.CENTER);
        
        painelPontuacoes.revalidate();
        painelPontuacoes.repaint();
    }
    

}
   
    