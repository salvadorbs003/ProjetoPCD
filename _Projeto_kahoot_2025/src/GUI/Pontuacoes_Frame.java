package GUI;

import javax.swing.*;

import GameState.Lista_Jogadores;
import Perguntas.Lista_Perguntas;

import java.awt.*;

import java.util.ArrayList;
import java.util.List;

public class Pontuacoes_Frame {
    private JFrame frame;
    List<Pergunta> perguntas = new ArrayList<>();
    private int perguntaAtualId;
    private boolean avancou = false;

    public Pontuacoes_Frame( int destaque, int perguntaAtualId) {
    	this.perguntaAtualId = perguntaAtualId;
        frame = new JFrame("Kahoot - Pontuações");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(45, 25, 120));
        frame.setLayout(new BorderLayout());
        
        String[] nomes = Lista_Jogadores.getNomes();
        int[] pontos = Lista_Jogadores.getPontuacoes();

        // PAINEL DO TOPO (título e botão à direita) - CORRIGIDO
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setBackground(new Color(45, 25, 120));
        painelTopo.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // Mais margem
        painelTopo.setPreferredSize(new Dimension(100, 100)); // Altura fixa para garantir visibilidade
        
        JLabel titulo = new JLabel("Pontuações", JLabel.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 48));
        titulo.setForeground(Color.WHITE);

        // BOTÃO AVANÇAR - CORRIGIDO

        JButton avancar = new JButton("AVANÇAR");
        avancar.setFont(new Font("Arial", Font.BOLD, 20));
        avancar.setBackground(Color.WHITE); // Fundo branco
        avancar.setForeground(Color.BLACK); // Letras pretas
        avancar.setFocusPainted(false);
        avancar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efeito hover para melhor visualização
        avancar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                avancar.setBackground(new Color(255, 215, 0)); // Amarelo mais claro
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                avancar.setBackground(new Color(255, 193, 7)); // Amarelo original
            }
        });

        painelTopo.add(titulo, BorderLayout.CENTER);
        painelTopo.add(avancar, BorderLayout.EAST);

        frame.add(painelTopo, BorderLayout.NORTH);

        // PAINEL CENTRAL: pontuações (mantém igual)
        JPanel painelCentral = new JPanel(new GridLayout(nomes.length, 1, 0, 10));
        painelCentral.setBackground(new Color(45, 25, 120));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(40, 400, 120, 400));

        int max = Math.max(1, java.util.Arrays.stream(pontos).max().orElse(1));

        for (int i = 0; i < nomes.length; i++) {
            JPanel linha = new JPanel(new BorderLayout());
            linha.setBackground(i == destaque ? Color.WHITE : new Color(80, 70, 150));
            linha.setBorder(BorderFactory.createEmptyBorder(7, 30, 7, 30));
            
            JLabel nome = new JLabel((i + 1) + "  " + nomes[i]);
            nome.setFont(new Font("Arial", Font.BOLD, 20));
            nome.setForeground(i == destaque ? new Color(45, 25, 120) : Color.WHITE);
            linha.add(nome, BorderLayout.WEST);
            
            JProgressBar barra = new JProgressBar(0, max);
            barra.setValue(pontos[i]);
            barra.setStringPainted(false);
            barra.setForeground(i == destaque ? new Color(45, 25, 120) : new Color(210, 210, 220));
            barra.setBackground(new Color(130, 130, 190));
            barra.setPreferredSize(new Dimension(230, 12));
            linha.add(barra, BorderLayout.CENTER);
            
            JLabel lblScore = new JLabel(String.valueOf(pontos[i]));
            lblScore.setFont(new Font("Arial", Font.BOLD, 20));
            lblScore.setForeground(i == destaque ? new Color(45, 25, 120) : Color.WHITE);
            linha.add(lblScore, BorderLayout.EAST);
            
            painelCentral.add(linha);
        }
        frame.add(painelCentral, BorderLayout.CENTER);

        frame.setVisible(true);
        avancar.addActionListener(e -> {
            frame.dispose();
            avancarParaProximaPergunta();
        });

        new Thread(() -> {
            try {
                Thread.sleep(10000);
                if (frame.isVisible()) {
                    SwingUtilities.invokeLater(() -> {
                        frame.dispose();
                        avancarParaProximaPergunta();
                    });
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void avancarParaProximaPergunta() {
    	
    	
        List<Pergunta> todasPerguntas = Lista_Perguntas.getPerguntas();
        
        if (todasPerguntas == null || todasPerguntas.isEmpty()) {
            System.err.println("Nenhuma pergunta carregada! A terminar quiz.");
            new Classificacao_Final_Frame(0, "Quiz Final");
            return;
        }
        
        int proximoId = perguntaAtualId + 1;
        Pergunta prox = todasPerguntas.stream()
                .filter(p -> p.getId() == proximoId)
                .findFirst()
                .orElse(null);

        if (prox != null) {
            new Perguntas_Frame(prox, proximoId);
        } else {
        	new Classificacao_Final_Frame(
        		    0,
        		    "Quiz Final"
        		);
        }
    }
    
   
}