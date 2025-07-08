package rpg.core;

import rpg.player.Player;
import rpg.rooms.Room;
import rpg.rooms.town.Town;
import rpg.gui.main.GameGUI;

public class Game {

    private Player player;
    private Room currentRoom;
    private GameGUI gui;
    private boolean isRunning;

    public Game() {
        this.player = new Player("Vestappen");
        this.currentRoom = new Town();
        this.isRunning = false;
    }

    public void startGame() {
        gui = new GameGUI(this);
        isRunning = true;

        gui.displayMessage("====Welcome to Paradox Protocol====");
        gui.displayMessage("Type 'help' for command suggestions");
        gui.displayMessage("");

        currentRoom.enter(this);
    }

    public void processCommand(String input) {
        if (!isRunning) {
            return;
        }

        String[] parts = input.trim().toLowerCase(). split(" ");
        String command = parts[0];

        switch (command) {
            case "help":
                showHelp();
                break;
            case "look":
                currentRoom.look(this);
                break;
            case "stats":
                showStats();
                break;
            case "quit":
                quit();
                break;
            default:
                gui.displayMessage("Unknown Command. Type 'help' for suggestions.");
        }
    }

    private void showHelp() {
        gui.displayMessage("Available commands:");
        gui.displayMessage("- help: Show this help message");
        gui.displayMessage("- look: Look around the current area");
        gui.displayMessage("- stats: Show your character stats");
        gui.displayMessage("- quit: Exit the game");
    }

    private void showStats() {
        gui.displayMessage("=== Character Stats ===");
        gui.displayMessage("Name: " + player.getName());
        gui.displayMessage("Level: " + player.getLevel());
        gui.displayMessage("HP: " + player.getHp() + "/" + player.getMaxHp());
        gui.displayMessage("Gold: " + player.getGold());
    }

    private void quit() {
        gui.displayMessage("Thanks for playing!");
        isRunning = false;
        System.exit(0);
    }

    // Getters
    public Player getPlayer() { return player; }
    public Room getCurrentRoom() { return currentRoom; }
    public GameGUI getGui() { return gui; }
    public boolean isRunning() { return isRunning; }
}