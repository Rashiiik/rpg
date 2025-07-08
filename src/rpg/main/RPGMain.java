package rpg.main;

import rpg.core.Game;
import javax.swing.SwingUtilities;

public class RPGMain {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Game().startGame();
        });
    }
}
