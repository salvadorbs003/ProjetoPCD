package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import Cliente.Cliente;
import Protocolos.NextQuestion;

public class Nomes_EntrarJogo_Frame {

    private JFrame frame;
    private JTextField nomeField;
    private JTextField equipaField;
    private JButton okButton;
    private JPanel p1, p2, p3, p4;
    private String pin; 
    
    // Server connection details
    String host = "localhost";
    int port = 12345;
    Cliente cliente;

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
        this.cliente = new Cliente(host, port, pin, equipa, nome);

        if (nome.isEmpty() || nome.startsWith("Insere") ||
                equipa.isEmpty() || equipa.startsWith("Insere")) {
            JOptionPane.showMessageDialog(frame, "Preenche o nome e o nome da equipa!");
            return;
        }

        // Tenta validação básica com o servidor
        if (!cliente.validarSala()) {
            JOptionPane.showMessageDialog(frame, "PIN inválido!");
            return;
        }
        
        // Verifica estado da equipa e se jogador já existe
        int estadoEquipa = cliente.verificarEstadoEquipa(equipa);
        
        // Se verificarJogador retornar true, significa que NÃO existe (sucesso na validação prévia)
        // Nota: Ajuste conforme a lógica do seu método verificarJogador (se retorna true para "livre" ou "ocupado")
        // Assumindo que sua lógica atual é: true se pode entrar/não existe.
        if (cliente.verificarJogador(nome, pin)) { 
            
            if (estadoEquipa == 0) {
                // Equipa nova ou vazia (espera pelo 2º jogador)
                int resposta = JOptionPane.showConfirmDialog(frame,
                        "Ligado com sucesso à sala!\n\n" +
                                "Equipa incompleta, falta 1 membro!\n" +
                                "  - Equipa: " + equipa + "\n" +
                                "  - Estado: 1/2 jogadores\n\n" +
                                "À espera que outro jogador se junte...",
                        "Estado da Equipa",
                        JOptionPane.YES_NO_OPTION);

                if (resposta == JOptionPane.YES_OPTION) {
                    okButton.setEnabled(false);
                    okButton.setText("À espera do teu parceiro...");

                    new Thread(() -> {
                        boolean jogoIniciou = cliente.ligar(); 
                        if (jogoIniciou) {
                            SwingUtilities.invokeLater(() -> {
                                iniciarJogo(nome, equipa);
                                frame.dispose();
                            });
                        }
                    }).start();
                } else {
                    frame.dispose();
                }
            } else if (estadoEquipa == 1) {
                // Equipa tem 1 jogador, eu sou o 2º (Equipa fica completa)
                okButton.setEnabled(false);
                okButton.setText("Equipa completa! À espera de adversários...");

                new Thread(() -> {
                    boolean jogoIniciou = cliente.ligar(); 
                    if (jogoIniciou) {
                        SwingUtilities.invokeLater(() -> {
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
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Não foi possível obter o estado da equipa. Tenta novamente.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame,
                    "Já existe um jogador no lobby com o nome: " + nome,
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarJogo(String nome, String equipa) {
        new Thread(() -> {
            // Request the first question (which returns a generic Mensagem now)
            Object response = cliente.enviarGameStartRequest(pin);

            SwingUtilities.invokeLater(() -> {
                if (response instanceof NextQuestion) {
                    frame.dispose(); 
                    new Pergunta_Respostas_Frame((NextQuestion) response, cliente);
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Erro ao obter a pergunta do servidor.",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            });
        }).start();
    }
}