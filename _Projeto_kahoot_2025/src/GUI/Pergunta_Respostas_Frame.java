package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import GameState.Lista_Jogadores;

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


    public Pergunta_Respostas_Frame(int indicePergunta, Pergunta pergunta,  java.util.List<String> opcoes, int tempoRestante) {
    	this.pergunta = pergunta;
    	this.tempoRestante = tempoRestante;
    	this.opcoes = opcoes;
    	this.jogadorId = Lista_Jogadores.getIdJogadorAtual();
    	
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
        p2.setBorder(BorderFactory.createEmptyBorder(80, 200, 100, 200));

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
                     terminarRonda();
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
        
        int jogadorId = 0;
        if (correta) {
            pontos = tempoRestante * 10;
            Lista_Jogadores.adicionarPontos(jogadorId, pontos);
            System.out.println("Resposta Correta! + " + pontos + " pontos.");
        } else {
            System.out.println("Resposta Errada! 0 pontos.");
        }
        
        terminarRonda();
    }

    private void terminarRonda() {
        respondeu = true;
        if (temporizadorThread != null && temporizadorThread.isAlive()) {
            temporizadorThread.interrupt();
        }
        
        frame.dispose();
        String[] nomes = {"Laura", "Salvador", "Maria", "Pedro"};
        int[] pontos = {150, 120, 90, 60};
        int destaque = 0;
        
        int perguntaAtualId = pergunta.getId(); // <--- precisas guardar esta pergunta como atributo da classe
        new Pontuacoes_Frame(destaque, perguntaAtualId);
        
        //aqui a ronda termina e 
        //  depois vai passar para a tela de pontuações
    }

   
    
}
