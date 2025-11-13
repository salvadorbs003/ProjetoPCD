package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import Cliente.Cliente;

public class Codigo_PINInicial_Frame {
	
	private JFrame frame;
    private JTextField campoPin;
    private JButton botaoInserir;

    public Codigo_PINInicial_Frame() {

        frame = new JFrame("Kahoot - Frame PIN do Jogo");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //é neste panel que vamos colocar os elementos da tela
        JPanel fundo_roxo = new JPanel();
        fundo_roxo.setBackground(new Color(45, 25, 120));
        //usamos o border layou
        fundo_roxo.setLayout(new BorderLayout());
        
        
        JLabel titulo = new JLabel("Kahoot!", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 80));
        titulo.setForeground(Color.WHITE);
        

        JPanel caixa = new JPanel();
        caixa.setLayout(new GridLayout(2, 1, 0, 10));
        caixa.setBackground(Color.WHITE);

        caixa.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createLineBorder(new Color(200, 200, 200), 2),   
        	    BorderFactory.createEmptyBorder(60, 80, 60, 80)                
        	));

        campoPin = new JTextField("PIN do jogo");
        campoPin.setFont(new Font("Arial", Font.PLAIN, 15));
        campoPin.setHorizontalAlignment(JTextField.CENTER);
        campoPin.setForeground(Color.GRAY);
        campoPin.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 2));
        campoPin.setPreferredSize(new Dimension(300, 50));

        campoPin.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (campoPin.getText().equals("PIN do jogo")) {
                    campoPin.setText("");
                    campoPin.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent e) {
                if (campoPin.getText().isEmpty()) {
                    campoPin.setText("PIN do jogo");
                    campoPin.setForeground(Color.GRAY);
                }
            }
        });

        botaoInserir = new JButton("Inserir");
        botaoInserir.setBackground(Color.BLACK);
        botaoInserir.setForeground(Color.WHITE);
        botaoInserir.setOpaque(true);
        botaoInserir.setFont(new Font("Arial", Font.BOLD, 18));
        botaoInserir.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        botaoInserir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botaoInserir.setFocusPainted(false);

        caixa.add(campoPin);
        caixa.add(botaoInserir);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(caixa);

        //isto é so para por o titulo mais para baixo
        JPanel painelTitulo = new JPanel(new BorderLayout());
        painelTitulo.setBackground(new Color(45, 25, 120)); 
        painelTitulo.setBorder(BorderFactory.createEmptyBorder(200, 0, 0, 0)); 
        painelTitulo.add(titulo, BorderLayout.CENTER);

        fundo_roxo.add(painelTitulo, BorderLayout.NORTH);        
        fundo_roxo.add(centro, BorderLayout.CENTER);

        frame.add(fundo_roxo);
        frame.setVisible(true);

        botaoInserir.addActionListener(e -> {
            String pin = campoPin.getText().trim();

            if (pin.isEmpty() || pin.equals("PIN do jogo")) {
                JOptionPane.showMessageDialog(frame, "Por favor, insere o PIN do jogo!");
                return;
            }

            // Dados fixos só para testar (depois podem vir de outra GUI)
            String host = "localhost";
            int port = 12345;
            
            

            // Tenta ligar ao servidor com o PIN inserido
            Cliente cliente = new Cliente(host, port, pin, "", "");
            boolean ligado = cliente.validarSala();

            if (ligado) {
                JOptionPane.showMessageDialog(frame, " Ligado com sucesso à sala " + pin + "!");
                frame.dispose();
                new Nomes_EntrarJogo_Frame(pin);
            } else {
                JOptionPane.showMessageDialog(frame, " PIN inválido ou sala inexistente!");
            }
        });
    }

    
    

}