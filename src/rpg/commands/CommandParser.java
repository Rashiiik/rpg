package rpg.commands;

import rpg.core.Game;
import rpg.commands.general.HelpCommand;
import rpg.commands.general.StatsCommand;
import rpg.commands.movement.LookCommand;
import rpg.commands.movement.GoCommand;
import rpg.commands.items.InventoryCommand;
import rpg.commands.items.UseCommand;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

public class CommandParser {
    private Map<String, Command> commands;
    private Game game;

    public CommandParser(Game game) {
        this.game = game;
        this.commands = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        // Register all commands
        registerCommand("help", new HelpCommand());
        registerCommand("look", new LookCommand());
        registerCommand("stats", new StatsCommand());
        registerCommand("go", new GoCommand());
        registerCommand("inventory", new InventoryCommand());
        registerCommand("use", new UseCommand());

        // Add more commands as you implement them
        // registerCommand("take", new TakeCommand());
        // registerCommand("equip", new EquipCommand());
        // registerCommand("attack", new AttackCommand());
        // registerCommand("buy", new BuyCommand());
        // registerCommand("sell", new SellCommand());
    }

    private void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);

        // Also register any aliases
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public void parseAndExecute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] parts = input.trim().toLowerCase().split("\\s+");
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(game, args);
            } catch (Exception e) {
                game.getGui().displayMessage("Error executing command: " + e.getMessage());
            }
        } else {
            game.getGui().displayMessage("Unknown command: " + commandName);
            game.getGui().displayMessage("Type 'help' for available commands.");
        }
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void addCommand(String name, Command command) {
        registerCommand(name, command);
    }
}