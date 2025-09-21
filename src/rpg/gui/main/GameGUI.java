package rpg.gui.main;

import rpg.core.Game;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {
    private Game game;
    private JTextPane gameOutput;
    private StyledDocument doc;
    private JTextField commandInput;
    private JScrollPane scrollPane;
    private JPanel statusPanel;
    private JLabel statusLabel;
    private List<String> commandHistory;
    private int historyIndex;

    private static final Color BACKGROUND_COLOR = new Color(18, 18, 18);
    private static final Color OUTPUT_BACKGROUND = new Color(25, 25, 25);
    private static final Color INPUT_BACKGROUND = new Color(35, 35, 35);
    private static final Color TEXT_COLOR = new Color(220, 220, 220);
    private static final Color ACCENT_COLOR = new Color(100, 200, 100);
    private static final Color PROMPT_COLOR = new Color(255, 215, 0);
    private static final Color BORDER_COLOR = new Color(60, 60, 60);

    public GameGUI(Game game) {
        this.game = game;
        this.commandHistory = new ArrayList<>();
        this.historyIndex = 0;
        initializeGUI();
    }

    private void initializeGUI() {
        setTitle("Touch Grass - RPG Adventure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        try {
            setIconImage(createGameIcon());
        } catch (Exception e) {

        }

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BACKGROUND_COLOR);

        createHeaderPanel();

        createGameOutputArea();

        createInputArea();

        setVisible(true);
        commandInput.requestFocus();

        displayWelcomeMessage();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel titleLabel = new JLabel("Touch Grass");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("A Text-Based RPG Adventure");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        subtitleLabel.setForeground(TEXT_COLOR.darker());
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titleContainer = new JPanel(new BorderLayout());
        titleContainer.setBackground(BACKGROUND_COLOR);
        titleContainer.add(titleLabel, BorderLayout.CENTER);
        titleContainer.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(titleContainer, BorderLayout.CENTER);

        JPanel separator = new JPanel();
        separator.setBackground(BORDER_COLOR);
        separator.setPreferredSize(new Dimension(0, 1));
        headerPanel.add(separator, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
    }

    private void createGameOutputArea() {
        gameOutput = new JTextPane();
        gameOutput.setEditable(false);
        gameOutput.setFont(new Font("Consolas", Font.PLAIN, 14));
        gameOutput.setBackground(OUTPUT_BACKGROUND);
        gameOutput.setForeground(TEXT_COLOR);
        gameOutput.setMargin(new Insets(20, 20, 20, 20));
        gameOutput.setBorder(BorderFactory.createEmptyBorder());

        doc = gameOutput.getStyledDocument();

        // Auto-scroll to bottom
        DefaultCaret caret = (DefaultCaret) gameOutput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        scrollPane = new JScrollPane(gameOutput);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getVerticalScrollBar().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setForeground(BORDER_COLOR);

        // Custom scrollbar UI
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = BORDER_COLOR;
                this.trackColor = BACKGROUND_COLOR;
            }
        });

        JPanel outputContainer = new JPanel(new BorderLayout());
        outputContainer.setBackground(BACKGROUND_COLOR);
        outputContainer.setBorder(new EmptyBorder(10, 20, 10, 20));
        outputContainer.add(scrollPane, BorderLayout.CENTER);

        add(outputContainer, BorderLayout.CENTER);
    }

    private void createInputArea() {
        JPanel inputContainer = new JPanel(new BorderLayout());
        inputContainer.setBackground(BACKGROUND_COLOR);
        inputContainer.setBorder(new EmptyBorder(10, 20, 15, 20));

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBackground(INPUT_BACKGROUND);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel promptLabel = new JLabel("> ");
        promptLabel.setFont(new Font("Consolas", Font.BOLD, 16));
        promptLabel.setForeground(PROMPT_COLOR);

        commandInput = new JTextField();
        commandInput.setFont(new Font("Consolas", Font.PLAIN, 14));
        commandInput.setBackground(INPUT_BACKGROUND);
        commandInput.setForeground(ACCENT_COLOR);
        commandInput.setCaretColor(ACCENT_COLOR);
        commandInput.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        commandInput.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    navigateHistory(-1);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    navigateHistory(1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyTyped(KeyEvent e) {}
        });

        commandInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processInput();
            }
        });

        inputPanel.add(promptLabel, BorderLayout.WEST);
        inputPanel.add(commandInput, BorderLayout.CENTER);

        JLabel helpLabel = new JLabel("Use ↑↓ for command history | Type 'help' for commands");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        helpLabel.setForeground(TEXT_COLOR.darker());
        helpLabel.setHorizontalAlignment(SwingConstants.CENTER);

        inputContainer.add(inputPanel, BorderLayout.CENTER);
        inputContainer.add(helpLabel, BorderLayout.SOUTH);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(BACKGROUND_COLOR);
        southPanel.add(inputContainer, BorderLayout.CENTER);
        southPanel.add(createStatusPanel(), BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatusPanel() {
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(INPUT_BACKGROUND);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(5, 20, 5, 20)
        ));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_COLOR);

        JLabel versionLabel = new JLabel("v1.069");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        versionLabel.setForeground(TEXT_COLOR.darker());

        statusPanel.add(statusLabel, BorderLayout.WEST);
        statusPanel.add(versionLabel, BorderLayout.EAST);

        return statusPanel;
    }

    private void processInput() {
        String input = commandInput.getText().trim();
        if (!input.isEmpty()) {

            commandHistory.add(input);
            historyIndex = commandHistory.size();

            displayMessage("> " + input, ACCENT_COLOR);
            displayMessage("", TEXT_COLOR); // Add blank line for readability
            commandInput.setText("");

            updateStatus("Processing command...");

            game.processCommand(input);

            updateStatus("Ready");
        }
    }

    private void navigateHistory(int direction) {
        if (commandHistory.isEmpty()) return;

        historyIndex += direction;
        if (historyIndex < 0) {
            historyIndex = 0;
        } else if (historyIndex >= commandHistory.size()) {
            historyIndex = commandHistory.size();
            commandInput.setText("");
            return;
        }

        commandInput.setText(commandHistory.get(historyIndex));
    }

    public void displayMessage(String message) {
        displayMessage(message, TEXT_COLOR);
    }

    public void append(String message) {
        displayMessage(message, TEXT_COLOR);
    }

    public JTextPane getGameOutput() {
        return gameOutput;
    }

    public void displayMessage(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            try {
                SimpleAttributeSet attributeSet = new SimpleAttributeSet();
                StyleConstants.setForeground(attributeSet, color);
                StyleConstants.setFontFamily(attributeSet, "Consolas");
                StyleConstants.setFontSize(attributeSet, 14);

                doc.insertString(doc.getLength(), message + "\n", attributeSet);

                gameOutput.setCaretPosition(doc.getLength());
            } catch (BadLocationException e) {
                gameOutput.setText(gameOutput.getText() + message + "\n");
                gameOutput.setCaretPosition(gameOutput.getDocument().getLength());
            }
        });
    }

    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(status);
        });
    }

    public void clearOutput() {
        SwingUtilities.invokeLater(() -> {
            gameOutput.setText("");
        });
    }

    private void displayWelcomeMessage() {
        SwingUtilities.invokeLater(() -> {
            displayMessage("═══════════════════════════════════════════════════════════════", ACCENT_COLOR);
            displayMessage("  Welcome to Touch Grass - A Text-Based RPG Adventure!", ACCENT_COLOR);
            displayMessage("═══════════════════════════════════════════════════════════════", ACCENT_COLOR);
            displayMessage("");
            displayMessage("Type 'help' to see available commands.");
            displayMessage("Use the arrow keys (↑↓) to navigate through command history.");
            displayMessage("");
        });
    }

    private Image createGameIcon() {
        int size = 32;
        java.awt.image.BufferedImage icon = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(ACCENT_COLOR);
        g2d.fillOval(4, 4, size-8, size-8);
        g2d.setColor(BACKGROUND_COLOR);
        g2d.fillOval(8, 8, size-16, size-16);
        g2d.setColor(ACCENT_COLOR);
        g2d.drawString("TG", size/2-8, size/2+4);

        g2d.dispose();
        return icon;
    }
}