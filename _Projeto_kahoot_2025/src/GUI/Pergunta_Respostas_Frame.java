package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import GameState.Jogador;
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
    private JPanel listaEquipasPanel;
    private JPanel painelSubmissoes;
    private JPanel listaSubmissoesJogadores;
    private JPanel listaSubmissoesEquipas;
    private final Set<String> jogadoresResponderam = new LinkedHashSet<>();
    private final Set<String> equipasResponderam = new LinkedHashSet<>();
    int perguntaAtualId;
    private int indicePergunta;
    private final Color fundoPrincipal = new Color(45, 25, 120);
    private final Color fundoPainel = new Color(33, 18, 90);




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
        frame.getContentPane().setBackground(fundoPrincipal);

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setBackground(fundoPrincipal);
        p1.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        indiceLabel = new JLabel("Pergunta " + indicePergunta);
        indiceLabel.setForeground(Color.WHITE);
        indiceLabel.setFont(new Font("Arial", Font.BOLD, 26));

        tempoLabel = new JLabel("⏱️" + tempoRestante + "s", SwingConstants.RIGHT);
        tempoLabel.setForeground(Color.WHITE);
        tempoLabel.setFont(new Font("Arial", Font.BOLD, 26));

        perguntaLabel = new JLabel(pergunta.getTexto(), SwingConstants.CENTER);
        perguntaLabel.setForeground(Color.WHITE);
        perguntaLabel.setFont(new Font("Arial", Font.BOLD, 30));
        perguntaLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        p1.add(indiceLabel, BorderLayout.WEST);
        p1.add(tempoLabel, BorderLayout.EAST);
        p1.add(perguntaLabel, BorderLayout.SOUTH);

        JPanel p2 = new JPanel(new GridLayout(2, 2, 20, 20));
        p2.setBackground(fundoPrincipal);
        p2.setBorder(BorderFactory.createEmptyBorder(80, 120, 80, 220));

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
        construirPainelLateral();
        construirPainelSubmissoes();


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
        Jogador jogador = Lista_Jogadores.getJogadorPorId(jogadorId);
        String nomeJogador = jogador != null ? jogador.getNome() : "Jogador " + jogadorId;
        String nomeEquipa = jogador != null ? jogador.getEquipa() : null;
        registrarRespostaRecebida(nomeJogador, nomeEquipa);
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
    
    
    private void construirPainelLateral() {
        painelPontuacoes = new JPanel(new BorderLayout());
        painelPontuacoes.setBackground(fundoPainel);
        painelPontuacoes.setPreferredSize(new Dimension(360, 0));
        painelPontuacoes.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel titulo = new JLabel("Scoreboard", SwingConstants.LEFT);
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        painelPontuacoes.add(titulo, BorderLayout.NORTH);

        listaEquipasPanel = new JPanel();
        listaEquipasPanel.setLayout(new BoxLayout(listaEquipasPanel, BoxLayout.Y_AXIS));
        listaEquipasPanel.setBackground(fundoPainel);

        JScrollPane scroll = new JScrollPane(listaEquipasPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(12);

        painelPontuacoes.add(scroll, BorderLayout.CENTER);
        frame.add(painelPontuacoes, BorderLayout.EAST);
        atualizarPainelPontuacoes();
    }

    private void atualizarPainelPontuacoes() {
        if (painelPontuacoes == null || listaEquipasPanel == null) return;

        listaEquipasPanel.removeAll();
        List<TeamScore> equipas = new ArrayList<>(agruparPontuacoesPorEquipa().values());
        equipas.sort(Comparator.comparingInt((TeamScore e) -> e.totalPontos).reversed());

        if (equipas.isEmpty()) {
            JLabel vazio = new JLabel("Sem equipas para mostrar");
            vazio.setForeground(Color.WHITE);
            vazio.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaEquipasPanel.add(vazio);
        } else {
            int posicao = 1;
            for (TeamScore equipa : equipas) {
                listaEquipasPanel.add(criarCartaoEquipa(posicao, equipa));
                posicao++;
            }
        }

        listaEquipasPanel.revalidate();
        listaEquipasPanel.repaint();
    }

    private JPanel criarCartaoEquipa(int posicao, TeamScore equipaInfo) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(54, 34, 126));
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titulo = new JLabel("#" + posicao + " " + equipaInfo.nome + " · " + equipaInfo.totalPontos + " pts");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel jogadoresPanel = new JPanel();
        jogadoresPanel.setLayout(new BoxLayout(jogadoresPanel, BoxLayout.Y_AXIS));
        jogadoresPanel.setBackground(card.getBackground());
        jogadoresPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        List<Jogador> jogadoresOrdenados = new ArrayList<>(equipaInfo.jogadores);
        jogadoresOrdenados.sort(Comparator.comparingInt(Jogador::getPontuacao).reversed());

        for (Jogador j : jogadoresOrdenados) {
            JLabel jogadorLabel = new JLabel("• " + j.getNome() + " — " + j.getPontuacao() + " pts");
            jogadorLabel.setForeground(new Color(230, 230, 255));
            jogadorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            jogadoresPanel.add(jogadorLabel);
        }

        card.add(titulo, BorderLayout.NORTH);
        card.add(jogadoresPanel, BorderLayout.CENTER);
        card.add(new JSeparator(), BorderLayout.SOUTH);
        return card;
    }

    private Map<String, TeamScore> agruparPontuacoesPorEquipa() {
        Map<String, TeamScore> mapa = new LinkedHashMap<>();
        for (Jogador j : Lista_Jogadores.getJogadores()) {
            String nomeEquipa = j.getEquipa() != null ? j.getEquipa() : "Sem equipa";
            TeamScore equipa = mapa.computeIfAbsent(nomeEquipa, TeamScore::new);
            equipa.totalPontos += j.getPontuacao();
            equipa.jogadores.add(j);
        }
        return mapa;
    }

    private void construirPainelSubmissoes() {
        painelSubmissoes = new JPanel(new BorderLayout());
        painelSubmissoes.setBackground(new Color(28, 18, 80));
        painelSubmissoes.setBorder(BorderFactory.createEmptyBorder(14, 24, 18, 24));

        JLabel titulo = criarTituloSecundario("Envios desta pergunta");
        painelSubmissoes.add(titulo, BorderLayout.NORTH);

        JPanel colunas = new JPanel(new GridLayout(1, 2, 14, 0));
        colunas.setBackground(painelSubmissoes.getBackground());

        listaSubmissoesJogadores = criarListaSubmissoesPanel();
        listaSubmissoesEquipas = criarListaSubmissoesPanel();

        colunas.add(criarColunaSubmissoes("Jogadores que já enviaram", listaSubmissoesJogadores));
        colunas.add(criarColunaSubmissoes("Equipas com resposta", listaSubmissoesEquipas));
        painelSubmissoes.add(colunas, BorderLayout.CENTER);

        frame.add(painelSubmissoes, BorderLayout.SOUTH);
        atualizarPainelSubmissoes();
    }

    private JPanel criarColunaSubmissoes(String titulo, JPanel lista) {
        JPanel coluna = new JPanel(new BorderLayout());
        coluna.setBackground(new Color(35, 22, 98));
        coluna.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

        JLabel label = criarTituloSecundario(titulo);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        coluna.add(label, BorderLayout.NORTH);

        coluna.add(lista, BorderLayout.CENTER);

        return coluna;
    }

    private void atualizarPainelSubmissoes() {
        if (listaSubmissoesJogadores == null || listaSubmissoesEquipas == null) return;

        preencherListaSubmissoes(listaSubmissoesJogadores, jogadoresResponderam, "Nenhum jogador respondeu ainda");
        preencherListaSubmissoes(listaSubmissoesEquipas, equipasResponderam, "Nenhuma equipa respondeu ainda");

        painelSubmissoes.revalidate();
        painelSubmissoes.repaint();
    }

    private void preencherListaSubmissoes(JPanel coluna, Set<String> valores, String vazioMensagem) {
        coluna.removeAll();
        if (valores.isEmpty()) {
            JLabel vazio = new JLabel(vazioMensagem);
            vazio.setForeground(Color.WHITE);
            coluna.add(vazio);
            return;
        }
        for (String nome : valores) {
            coluna.add(criarChip(nome));
        }
    }

    private JPanel criarListaSubmissoesPanel() {
        JPanel lista = new JPanel(new GridLayout(0, 4, 8, 8));
        lista.setBackground(new Color(35, 22, 98));
        return lista;
    }

    private JPanel criarChip(String texto) {
        JPanel chip = new JPanel();
        chip.setBackground(new Color(76, 170, 255));
        chip.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        JLabel l = new JLabel(texto);
        l.setForeground(Color.WHITE);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        chip.add(l);
        return chip;
    }

    public void registrarRespostaRecebida(String jogadorNome, String equipaNome) {
        if (jogadorNome != null && !jogadorNome.isBlank()) {
            jogadoresResponderam.add(jogadorNome);
        }
        if (equipaNome != null && !equipaNome.isBlank()) {
            equipasResponderam.add(equipaNome);
        }
        atualizarPainelSubmissoes();
    }

    private JLabel criarTituloSecundario(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    private static class TeamScore {
        String nome;
        int totalPontos = 0;
        List<Jogador> jogadores = new ArrayList<>();

        TeamScore(String nome) {
            this.nome = nome;
        }
    }
}
    
