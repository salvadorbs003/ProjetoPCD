package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Cliente.Cliente;
import GameState.Jogador;
import GameState.Lista_Jogadores;
import GameState.QuizLoader;
import Perguntas.Lista_Perguntas;

public class Nomes_EntrarJogo_Frame {

    private JFrame frame;
    private JTextField nomeField;
    private JTextField equipaField;
    private JButton okButton;
    private JPanel p1, p2, p3, p4;
    private List<String> nomes = new ArrayList<>();
    private String pin; // <- vem da frame anterior

    public Nomes_EntrarJogo_Frame(String pin) {
    	
        this.pin = pin;
        frame = new JFrame("Kahoot - Entrar no Jogo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new GridLayout(4, 1));
        frame.getContentPane().setBackground(new Color(45, 25, 120));

        p1 = new JPanel();
        p1.setBackground(new Color(45, 25, 120));
        p1.setBorder(BorderFactory.createEmptyBorder(100, 0, 20, 0));

        JLabel titulo = new JLabel("Kahoot!");
        titulo.setFont(new Font("Arial", Font.BOLD, 60));
        titulo.setForeground(Color.WHITE);
        p1.add(titulo);

        p2 = new JPanel();
        p2.setBackground(new Color(45, 25, 120));
        p2.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));

        p3 = new JPanel();
        p3.setBackground(new Color(80, 70, 150));
        p3.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        p3.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));

        JLabel labelParticipar = new JLabel("Participar neste jogo");
        labelParticipar.setForeground(Color.WHITE);
        labelParticipar.setFont(new Font("Arial", Font.BOLD, 22));

        nomeField = criarCampoTexto("Insere o teu nome (ID pessoal)", 15);
        equipaField = criarCampoTexto("Insere o nome da tua equipa", 15);
        
        nomeField.setPreferredSize(new Dimension(350, 45));
        equipaField.setPreferredSize(new Dimension(350, 45));

        okButton = new JButton("Entrar no jogo");
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(Color.BLACK);
        okButton.setFont(new Font("Arial", Font.BOLD, 16));
        okButton.setFocusPainted(false);

        p3.add(labelParticipar);
        p3.add(nomeField);
        p3.add(equipaField);

        p4 = new JPanel();
        p4.setBackground(new Color(80, 70, 150));
        p4.add(okButton);

        frame.add(p1);
        frame.add(p2);
        frame.add(p3);
        frame.add(p4);
        frame.setVisible(true);

        okButton.addActionListener(e -> tentarEntrar());
    }

    private JTextField criarCampoTexto(String texto, int colunas) {
        JTextField campo = new JTextField(texto, colunas);
        campo.setHorizontalAlignment(JTextField.CENTER);
        campo.setForeground(Color.GRAY);
        campo.setFont(new Font("Arial", Font.PLAIN, 16));
        campo.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campo.getText().equals(texto)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(texto);
                    campo.setForeground(Color.GRAY);
                }
            }
        });

        return campo;
    }

    private void tentarEntrar() {
        String nome = nomeField.getText().trim();
        String equipa = equipaField.getText().trim();

        if (nome.isEmpty() || nome.startsWith("Insere") ||
            equipa.isEmpty() || equipa.startsWith("Insere")) {
            JOptionPane.showMessageDialog(frame, "Preenche o nome e o nome da equipa!");
            return;
        }

        // tenta ligação ao servidor
        String host = "localhost";
        int port = 12345;
        Cliente cliente = new Cliente(host, port, pin, equipa, nome);
        
        if (!cliente.validarSala()) {
            JOptionPane.showMessageDialog(frame, "PIN inválido!");
            return;
        }
        int estadoEquipa = cliente.verificarEstadoEquipa(equipa);
        System.out.println(estadoEquipa);
        
        if (estadoEquipa == 0) {
        	 int resposta = JOptionPane.showConfirmDialog(frame, 
                     "Ligado com sucesso à sala!\n\n" +
                     "Equipa incompleta, falta 1 membro!\n" +
                     "  - Equipa: " + equipa + "\n" +
                     "  - Estado: 1/2 jogadores\n\n" +
                     "À espera que outro jogador se junte...",
                     "Estado da Equipa",
                     JOptionPane.YES_NO_OPTION);

                 if (resposta == JOptionPane.YES_OPTION) {
                     new Thread(() -> {
                         boolean jogoIniciou = cliente.ligar();
                         if (jogoIniciou) {
                             SwingUtilities.invokeLater(() -> {
                                 JOptionPane.showMessageDialog(frame, 
                                     " Todas as equipas prontas! A iniciar jogo...");
                                 iniciarJogo(nome, equipa);
                                 frame.dispose();
                             });
                         }
                     }).start();
                 } else {
                     frame.dispose();
                 }

        } else if (estadoEquipa==1) {
        	JOptionPane.showMessageDialog(frame,
                " Ligado com sucesso!\n\n" +
                "Equipa completa! 2/2 jogadores\n" +
                "À espera que a 2ª equipa fique completa...");

            // Em vez de consultar estado global, aguardamos pela notificação JOGO_INICIAR do servidor
            // através do mesmo fluxo usado para equipas incompletas.
            new Thread(() -> {
                boolean jogoIniciou = cliente.ligar();
                if (jogoIniciou) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(frame, 
                            " Todas as equipas prontas! A iniciar jogo...");
                        iniciarJogo(nome, equipa);
                        frame.dispose();
                    });
                }
            }).start();
        } else if (estadoEquipa > 1) {
            JOptionPane.showMessageDialog(frame,
                "Esta equipa já se encontra completa",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        } else {
            JOptionPane.showMessageDialog(frame,
                "Não foi possível obter o estado da equipa. Tenta novamente.",
                "Erro",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
        		    
        private void iniciarJogo(String nome,  String equipa){
            Jogador jogador = new Jogador(nome, equipa);
            Lista_Jogadores.adicionarJogador(jogador);
            Lista_Jogadores.definirJogadorAtual(jogador);

            java.util.List<Pergunta> perguntas = QuizLoader.load("src/lista_perguntas.json");
            Lista_Perguntas.definirPerguntas(perguntas);
            
            frame.dispose();
            new Entrada_Jogo_Frame();
   }
}