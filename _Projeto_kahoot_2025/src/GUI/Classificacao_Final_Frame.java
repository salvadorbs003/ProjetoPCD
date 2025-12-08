package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import GameState.ScoreBoard;

public class Classificacao_Final_Frame {

    private JFrame frame;
    private final Color fundoRoxo = new Color(45, 25, 120);

    // --- CONSTRUCTOR: Uses ScoreBoard from the Server ---
    public Classificacao_Final_Frame(ScoreBoard sb, String meuNome, String nomeQuiz) {
        // 1. Extract data from ScoreBoard
        List<ScoreBoard.PlayerScore> scores = sb.getPlayerScores();
        
        String[] nomes = new String[scores.size()];
        int[] pontos = new int[scores.size()];
        int meuIndice = -1;

        for (int i = 0; i < scores.size(); i++) {
            nomes[i] = scores.get(i).getPlayerName();
            pontos[i] = scores.get(i).getPoints();
            
            // Find my index to highlight later
            if (nomes[i].equalsIgnoreCase(meuNome)) {
                meuIndice = i;
            }
        }

        // 2. Build UI
        initializeUI(nomes, pontos, meuIndice, nomeQuiz);
    }

    // --- SHARED UI LOGIC ---
    private void initializeUI(String[] nomes, int[] pontos, int meuIndice, String nomeQuiz) {
        frame = new JFrame("Kahoot - Classificação Final");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setBackground(fundoRoxo);
        frame.setLayout(new BorderLayout());

        // Cabeçalho
        JPanel topo = new JPanel();
        topo.setLayout(new BoxLayout(topo, BoxLayout.Y_AXIS));
        topo.setBackground(fundoRoxo);
        
        JLabel ka = new JLabel("Kahoot!", JLabel.CENTER);
        ka.setAlignmentX(Component.CENTER_ALIGNMENT);
        ka.setFont(new Font("Arial Black", Font.BOLD, 56));
        ka.setForeground(Color.WHITE);

        JLabel quizTitle = new JLabel(nomeQuiz, JLabel.CENTER);
        quizTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        quizTitle.setFont(new Font("Arial", Font.BOLD, 24));
        quizTitle.setForeground(Color.WHITE);
        quizTitle.setBorder(BorderFactory.createEmptyBorder(100, 0, 24, 0));
        
        topo.add(Box.createVerticalStrut(20));
        topo.add(ka);
        topo.add(quizTitle);
        frame.add(topo, BorderLayout.NORTH);

        // --- Validação
        if (nomes == null || pontos == null || nomes.length == 0) {
            JOptionPane.showMessageDialog(null, 
                "❌ Nenhum jogador encontrado para mostrar classificação final!",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            return;
        }

        if (nomes.length < 3) {
            System.err.println("⚠️ Menos de 3 jogadores — a classificação será parcial.");
        }

        // --- Ordenar índices (Sort indices based on points descending)
        Integer[] idxs = new Integer[nomes.length];
        for (int i = 0; i < nomes.length; i++) idxs[i] = i;
        
        // Note: ScoreBoard is usually sorted, but we sort again here to be safe
        final int[] pRef = pontos; 
        Arrays.sort(idxs, Comparator.comparingInt(i -> -pRef[(int)i]));

        // --- Valores do pódio
        int largura = 140;
        int altura1 = 310; // 1º lugar
        int altura2 = 220; // 2º lugar
        int altura3 = 150; // 3º lugar

        // --- Painel central pódio
        JPanel podium = new JPanel(new GridBagLayout());
        podium.setBackground(fundoRoxo);
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.SOUTH;
        c.insets = new Insets(0, 7, 0, 7);

        if (nomes.length >= 3) {
            // 2º lugar (esquerda)
            c.gridx = 0;
            c.gridy = 0;
            podium.add(podioPainel(nomes[idxs[1]], 2, pontos[idxs[1]], largura, altura2), c);

            // 1º lugar (centro)
            c.gridx = 1;
            c.gridy = 0;
            podium.add(podioPainel(nomes[idxs[0]], 1, pontos[idxs[0]], largura, altura1), c);

            // 3º lugar (direita)
            c.gridx = 2;
            c.gridy = 0;
            podium.add(podioPainel(nomes[idxs[2]], 3, pontos[idxs[2]], largura, altura3), c);
        } else {
            // Se houver menos de 3 jogadores, mostra apenas os existentes
            for (int i = 0; i < nomes.length; i++) {
                c.gridx = i;
                c.gridy = 0;
                int altura = altura1 - i * 50; 
                podium.add(podioPainel(nomes[idxs[i]], i + 1, pontos[idxs[i]], largura, altura), c);
            }
        }
        
        frame.add(podium, BorderLayout.CENTER);

        // --- Mensagem classificação do utilizador
        if (meuIndice != -1) {
            int minhaPosicao = -1;
            // Find rank in the sorted list
            for (int i = 0; i < nomes.length; i++) {
                if (idxs[i] == meuIndice) {
                    minhaPosicao = i + 1;
                    break;
                }
            }

            JPanel painelSul = new JPanel();
            painelSul.setBackground(fundoRoxo);
            JLabel msg;
            
            if (minhaPosicao <= 3) {
                msg = new JLabel("Parabéns! Ficaste no pódio em " + minhaPosicao + "º lugar com " + pontos[meuIndice] + " pontos!", JLabel.CENTER);
            } else {
                msg = new JLabel("Ficaste em " + minhaPosicao + "º lugar com " + pontos[meuIndice] + " pontos!", JLabel.CENTER);
            }
            msg.setForeground(Color.WHITE);
            msg.setFont(new Font("Arial", Font.BOLD, 24));
            msg.setBorder(BorderFactory.createEmptyBorder(18, 20, 100, 38));
            painelSul.add(msg);
            frame.add(painelSul, BorderLayout.SOUTH);
        }

        frame.setVisible(true);
    }

    // Painel de cada bloco do pódio
    private JPanel podioPainel(String nome, int lugar, int pontos, int largura, int altura) {
        JPanel podio = new JPanel();
        podio.setLayout(new BoxLayout(podio, BoxLayout.Y_AXIS));
        podio.setBackground(new Color(82, 90, 146));
        podio.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        podio.setPreferredSize(new Dimension(largura, altura));

        // Medalha ou número
        JLabel medalha = new JLabel(String.valueOf(lugar));
        medalha.setFont(new Font("Arial Black", Font.BOLD, 24));
        medalha.setOpaque(true);
        medalha.setForeground(Color.WHITE);
        if (lugar == 1) medalha.setBackground(new Color(255, 193, 7));
        else if (lugar == 2) medalha.setBackground(new Color(180, 180, 180));
        else medalha.setBackground(new Color(245, 90, 38));
        
        medalha.setHorizontalAlignment(JLabel.CENTER);
        medalha.setAlignmentX(Component.CENTER_ALIGNMENT);
        medalha.setMaximumSize(new Dimension(40, 32));
        
        podio.add(Box.createVerticalStrut(12));
        podio.add(medalha);

        // Nome
        JLabel labelNome = new JLabel(nome);
        labelNome.setFont(new Font("Arial Black", Font.PLAIN, 18));
        labelNome.setForeground(Color.WHITE);
        labelNome.setOpaque(false);
        labelNome.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelNome.setHorizontalAlignment(JLabel.CENTER);
        labelNome.setMaximumSize(new Dimension(largura, 24));
        
        podio.add(Box.createVerticalStrut(12));
        podio.add(labelNome);

        // Pontos
        JLabel labelPontos = new JLabel(pontos + " pts");
        labelPontos.setFont(new Font("Arial", Font.BOLD, 16));
        labelPontos.setForeground(Color.WHITE);
        labelPontos.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelPontos.setMaximumSize(new Dimension(largura, 24));
        
        podio.add(Box.createVerticalStrut(12));
        podio.add(labelPontos);

        return podio;
    }
}