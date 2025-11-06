package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Perguntas_Frame {
    private JFrame frame;
    private JLabel perguntaLabel;
    private JLabel timerLabel;
    private JLabel indiceLabel;
    private Timer timer;
    private int segundos = 5;

    public Perguntas_Frame(Pergunta pergunta, int indicePergunta) {

        frame = new JFrame("Pergunta");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 1)); 

        JPanel topo = new JPanel(new BorderLayout());
        topo.setBackground(new Color(45, 25, 120));
        
        indiceLabel = new JLabel("Pergunta " + indicePergunta);
        indiceLabel.setForeground(Color.WHITE);
        indiceLabel.setFont(new Font("Arial", Font.BOLD, 32));
        indiceLabel.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 0));
        topo.add(indiceLabel, BorderLayout.NORTH);

        JPanel perguntaPanel = new JPanel(new GridBagLayout());
        perguntaPanel.setOpaque(false);
        perguntaLabel = new JLabel(pergunta.getTexto(), SwingConstants.CENTER);
        perguntaLabel.setFont(new Font("Arial", Font.BOLD, 44));
        perguntaLabel.setForeground(Color.WHITE);
        perguntaPanel.add(perguntaLabel);
        topo.add(perguntaPanel, BorderLayout.CENTER);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(new Color(80, 70, 150));
        timerLabel = new JLabel("" + segundos, SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 64));
        timerLabel.setForeground(Color.WHITE);
        fundo.add(timerLabel);

        frame.add(topo);
        frame.add(fundo);
        frame.setVisible(true);

        startTimer(pergunta, indicePergunta);
    }

    private void startTimer(Pergunta pergunta, int indicePergunta) {
        timer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                segundos--;
                if (segundos >= 0) {
                    timerLabel.setText("" + segundos);
                } else {
                    timer.stop();
                    frame.dispose();
                    SwingUtilities.invokeLater(() -> 
                        new Pergunta_Respostas_Frame(
                            indicePergunta,
                            pergunta,
                            pergunta.getOpcoes(),
                            pergunta.getTempo()
                        )
                    );
                }
            }
        });
        timer.start();
    }

    
}
