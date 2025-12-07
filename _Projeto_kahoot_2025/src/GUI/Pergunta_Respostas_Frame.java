package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import Cliente.Cliente;
import GameState.ScoreBoard;
import Protocolos.NextQuestion;
import Quizz.Pergunta;

public class Pergunta_Respostas_Frame {

    private JFrame frame;
    private JLabel labelTimer;
    private JLabel labelQuestion;
    private JPanel panelAnswers;
    
    private Timer timer;
    private int remainingTime;
    
    // Data
    private final NextQuestion questionData;
    private final Pergunta pergunta;
    private final Cliente client; 

    // Kahoot Colors
    private final Color COLOR_BG = new Color(70, 23, 143); // Deep Purple
    private final Color COLOR_BTN_RED = new Color(226, 27, 60);
    private final Color COLOR_BTN_BLUE = new Color(19, 104, 206);
    private final Color COLOR_BTN_YELLOW = new Color(216, 158, 0);
    private final Color COLOR_BTN_GREEN = new Color(38, 137, 12);
    private final Color[] BTN_COLORS = {COLOR_BTN_RED, COLOR_BTN_BLUE, COLOR_BTN_YELLOW, COLOR_BTN_GREEN};

    public Pergunta_Respostas_Frame(NextQuestion data, Cliente client) {
        this.questionData = data;
        this.client = client;
        
        // Extract data from the Protocol object
        this.pergunta = data.getPergunta();
        this.remainingTime = data.getTime();

        initializeUI();
        startTimer();
    }

    private void initializeUI() {
        // Accessing ID via the Pergunta object now
        frame = new JFrame("Kahoot - Round " + questionData.getRound() + " | Question " + pergunta.getId());
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(COLOR_BG);

        // --- 1. HEADER (Top) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Timer Circle (Left)
        labelTimer = new JLabel(String.valueOf(remainingTime), SwingConstants.CENTER);
        labelTimer.setFont(new Font("Arial", Font.BOLD, 30));
        labelTimer.setForeground(Color.WHITE);
        labelTimer.setOpaque(true);
        labelTimer.setBackground(new Color(50, 0, 100));
        labelTimer.setPreferredSize(new Dimension(80, 80));
        labelTimer.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        
        // Question Text (Center) - Accessed via Pergunta object
        labelQuestion = new JLabel("<html><center>" + pergunta.getTexto() + "</center></html>", SwingConstants.CENTER);
        labelQuestion.setFont(new Font("Arial", Font.BOLD, 36));
        labelQuestion.setForeground(Color.WHITE);
        
        headerPanel.add(labelTimer, BorderLayout.WEST);
        headerPanel.add(labelQuestion, BorderLayout.CENTER);
        
        frame.add(headerPanel, BorderLayout.NORTH);

        // --- 2. ANSWERS (Center) ---
        panelAnswers = new JPanel(new GridLayout(2, 2, 15, 15));
        panelAnswers.setBackground(COLOR_BG);
        panelAnswers.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        List<String> options = pergunta.getOptions();
        for (int i = 0; i < options.size(); i++) {
            JButton btn = createAnswerButton(options.get(i), BTN_COLORS[i % 4], i);
            panelAnswers.add(btn);
        }

        frame.add(panelAnswers, BorderLayout.CENTER);

        // --- 3. SCOREBOARD (Right Sidebar) ---
        if (questionData.getScoreboard() != null) {
            JPanel sidebar = createScoreboardPanel();
            frame.add(sidebar, BorderLayout.EAST);
        }

        frame.setVisible(true);
    }

    private JButton createAnswerButton(String text, Color color, int index) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 24));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFocusPainted(false);
        
        btn.addActionListener(e -> submitAnswer(index));
        return btn;
    }

    private JPanel createScoreboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(45, 15, 95)); // Darker purple
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ScoreBoard sb = questionData.getScoreboard();
        
        // Determine Title based on ScoreBoard Type
        String titleText = (sb.getType() == ScoreBoard.ScoreType.TEAM) ? "Team Leaderboard" : "Player Leaderboard";
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(title);
        panel.add(Box.createVerticalStrut(15));

        // Render List based on ScoreBoard Type
        if (sb.getType() == ScoreBoard.ScoreType.TEAM) {
            // Render Team Scores
            for (ScoreBoard.TeamScore ts : sb.getTeamScores()) {
                addScoreCard(panel, ts.getTeamName(), ts.getPoints());
            }
        } else {
            // Render Player Scores
            for (ScoreBoard.PlayerScore ps : sb.getPlayerScores()) {
                // You can include team name in display if you want: ps.getPlayerName() + " (" + ps.getTeamName() + ")"
                addScoreCard(panel, ps.getPlayerName(), ps.getPoints());
            }
        }
        
        // Wrap in ScrollPane
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(280, 0));
        wrapper.add(new JScrollPane(panel), BorderLayout.CENTER);
        return wrapper;
    }

    // Helper method to create a visual card for a score entry
    private void addScoreCard(JPanel panel, String name, int points) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(250, 40));
        card.setPreferredSize(new Dimension(250, 40));
        card.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));
        nameLbl.setForeground(Color.BLACK);
        
        JLabel scoreLbl = new JLabel(String.valueOf(points));
        scoreLbl.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLbl.setForeground(new Color(70, 23, 143)); // Purple text for points

        card.add(nameLbl, BorderLayout.WEST);
        card.add(scoreLbl, BorderLayout.EAST);

        panel.add(card);
        panel.add(Box.createVerticalStrut(8));
    }

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                remainingTime--;
                labelTimer.setText(String.valueOf(remainingTime));

                if (remainingTime <= 0) {
                    timer.stop();
                    disableButtons();
                    JOptionPane.showMessageDialog(frame, "Time's up!");
                    submitAnswer(-1); 
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    private void submitAnswer(int index) {
        timer.stop();
        disableButtons();
        
        if(index >= 0) {
            System.out.println("Selected answer index: " + index);
            // TODO: Call client method to send answer to server
            // client.enviarResposta(index); 
        } else {
            System.out.println("Time out / No answer");
        }
    }

    private void disableButtons() {
        for (Component c : panelAnswers.getComponents()) {
            if (c instanceof JButton) {
                c.setEnabled(false);
                ((JButton)c).setBackground(Color.GRAY);
            }
        }
    }
}