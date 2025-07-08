package rpg.gui.main;

import rpg.core.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI extends JFrame {
    private Game game;
    private JTextArea gameOutput;
    private JTextField commandInput;
    private JScrollPane scrollPane;

    public GameGUI(Game game) {
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Paradox Protocol");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false); // Make window non-resizable

        // Create main layout
        setLayout(new BorderLayout());

        // Game output area - Zork style
        gameOutput = new JTextArea();
        gameOutput.setEditable(false);
        gameOutput.setFont(new Font("Courier New", Font.PLAIN, 14)); // Classic monospace font
        gameOutput.setBackground(Color.BLACK);
        gameOutput.setForeground(Color.WHITE); // White text on black background
        gameOutput.setMargin(new Insets(15, 15, 15, 15));
        gameOutput.setLineWrap(true);
        gameOutput.setWrapStyleWord(true);
        gameOutput.setBorder(BorderFactory.createEmptyBorder());

        scrollPane = new JScrollPane(gameOutput);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setBackground(Color.BLACK);
        scrollPane.getVerticalScrollBar().setForeground(Color.WHITE);

        // Command input area - Zork style
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(Color.BLACK);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel promptLabel = new JLabel("> ");
        promptLabel.setFont(new Font("Courier New", Font.BOLD, 14));
        promptLabel.setForeground(Color.WHITE);
        promptLabel.setBackground(Color.BLACK);

        commandInput = new JTextField();
        commandInput.setFont(new Font("Courier New", Font.PLAIN, 14));
        commandInput.setBackground(Color.BLACK);
        commandInput.setForeground(Color.WHITE);
        commandInput.setCaretColor(Color.WHITE);
        commandInput.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        commandInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processInput();
            }
        });

        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(commandInput, BorderLayout.CENTER);

        // Add a subtle border between output and input
        JPanel separatorPanel = new JPanel();
        separatorPanel.setBackground(Color.DARK_GRAY);
        separatorPanel.setPreferredSize(new Dimension(800, 1));

        // Add components to frame
        add(scrollPane, BorderLayout.CENTER);
        add(separatorPanel, BorderLayout.SOUTH);
        add(inputPanel, BorderLayout.SOUTH);

        // Set frame background
        getContentPane().setBackground(Color.BLACK);

        // Show the window
        setVisible(true);
        commandInput.requestFocus();
    }

    private void processInput() {
        String input = commandInput.getText().trim();
        if (!input.isEmpty()) {
            // Display the command with a prompt
            displayMessage("> " + input);
            displayMessage(""); // Add blank line for readability
            commandInput.setText("");
            game.processCommand(input);
        }
    }

    public void displayMessage(String message) {
        gameOutput.append(message + "\n");
        gameOutput.setCaretPosition(gameOutput.getDocument().getLength());
    }

    public void clearOutput() {
        gameOutput.setText("");
    }
}