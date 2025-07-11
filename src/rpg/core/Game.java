package rpg.core;

import rpg.player.Player;
import rpg.rooms.Room;
import rpg.gui.main.GameGUI;
import rpg.commands.CommandParser;

public class Game {

    private Player player;
    private Room currentRoom;
    private GameGUI gui;
    private CommandParser commandParser;
    private RoomManager roomManager;
    private StoryFlags storyFlags;
    private boolean isRunning;

    public Game() {
        this.player = new Player("Vestappen");
        this.roomManager = new RoomManager();
        this.currentRoom = roomManager.getStartingRoom();
        this.storyFlags = new StoryFlags();
        this.isRunning = false;
    }

    public void startGame() {
        gui = new GameGUI(this);
        commandParser = new CommandParser(this); // Initialize command parser
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

        // Handle quit command directly (since it exits the game)
        if (input.trim().toLowerCase().equals("quit")) {
            quit();
            return;
        }

        // Use command parser for all other commands
        commandParser.parseAndExecute(input);
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
    public StoryFlags getStoryFlags() {
        return storyFlags;
    }
    public CommandParser getCommandParser() { return commandParser; }
    public RoomManager getRoomManager() { return roomManager; }
    public boolean isRunning() { return isRunning; }

    // Setters
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }
}