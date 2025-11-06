package GUI;

import javax.swing.*;

import GameState.QuizLoader;
import Perguntas.Lista_Perguntas;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class Entrada_Jogo_Frame {
	
    private JFrame frame;
    private JLabel labelContagem;
    private Timer timer;
    private int contagem = 3; // começa a contagem de 3 segundos
    private List<Pergunta> perguntas; // lista carregada com Gson

    public Entrada_Jogo_Frame() {
    	
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

        // 💡 Carrega as perguntas do ficheiro JSON (usando o Gson)
        perguntas = QuizLoader.load("src/lista_perguntas.json");

        if (perguntas == null || perguntas.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "❌ Erro: Nenhuma pergunta encontrada no ficheiro JSON!",
                "Erro no Quiz",
                JOptionPane.ERROR_MESSAGE);
            frame.dispose();
            return;
        }

        startContagem();
    }

    private void startContagem() {
        labelContagem.setText(contagem + "!");
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                contagem--;
                if (contagem > 0) {
                    labelContagem.setText(contagem + "!");
                } else {
                    timer.stop();
                    frame.dispose();

                    // agora pegamos da lista global com IDs corretos
                    List<Pergunta> todas = Lista_Perguntas.getPerguntas();
                    if (todas == null || todas.isEmpty()) {
                        JOptionPane.showMessageDialog(frame, "Nenhuma pergunta carregada!");
                        return;
                    }

                    Pergunta primeiraPergunta = todas.get(0);
                    int idPrimeira = primeiraPergunta.getId();

                    new Perguntas_Frame(primeiraPergunta, idPrimeira);
                }
            }
        });
        timer.start();
    }
}