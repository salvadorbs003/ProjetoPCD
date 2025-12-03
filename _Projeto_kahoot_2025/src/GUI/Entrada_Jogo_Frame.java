package GUI;

import javax.swing.*;

import GameState.ClientGameState;
import Quizz.Pergunta;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class Entrada_Jogo_Frame {
	
    private JFrame frame;
    private JLabel labelContagem;
    private Timer timer;
    private int contagem = 3; // começa a contagem de 3 segundos
    private final ClientGameState gameState;

    public Entrada_Jogo_Frame(ClientGameState gameState) {
        this.gameState = gameState;
    	
        frame = new JFrame("Início do Jogo");
        frame.setSize(400, 200);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel fundo_roxo = new JPanel();
        fundo_roxo.setBackground(new Color(45, 25, 120));
        fundo_roxo.setLayout(new BorderLayout());

        labelContagem = new JLabel("", SwingConstants.CENTER);
        labelContagem.setFont(new Font("Arial", Font.BOLD, 80));
        labelContagem.setForeground(Color.WHITE);

        fundo_roxo.add(labelContagem, BorderLayout.CENTER);
        frame.add(fundo_roxo);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        List<Pergunta> perguntas = gameState != null ? gameState.getPerguntas() : null;
        if (perguntas == null || perguntas.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "❌ Erro: Nenhuma pergunta encontrada no ficheiro JSON!",
                "Erro no Quiz",
                JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            return;
        }

       // startContagem();
    }
}
