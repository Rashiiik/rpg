package rpg.commands;

import rpg.commands.general.ExamineCommand;
import rpg.commands.items.TakeCommand;
import rpg.commands.shop.BuyCommand;
import rpg.commands.shop.SellCommand;
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
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class CommandParser {
    private Map<String, Command> commands;
    private Game game;

    // Common words to filter out
    private static final Set<String> FILLER_WORDS = new HashSet<>(Arrays.asList(
            "a", "an", "the", "i", "want", "would", "like", "please", "can",
            "could", "should", "will", "shall", "to", "at", "by", "for", "with", "from"
    ));

    // All possible command words (including aliases) mapped to their primary command
    private static final Map<String, String> ALL_COMMAND_WORDS = new HashMap<>();
    static {
        // Take command variants
        ALL_COMMAND_WORDS.put("take", "take");
        ALL_COMMAND_WORDS.put("get", "take");
        ALL_COMMAND_WORDS.put("grab", "take");
        ALL_COMMAND_WORDS.put("pickup", "take");
        ALL_COMMAND_WORDS.put("pick", "take");

        // Examine command variants
        ALL_COMMAND_WORDS.put("examine", "examine");
        ALL_COMMAND_WORDS.put("inspect", "examine");
        ALL_COMMAND_WORDS.put("study", "examine");
        ALL_COMMAND_WORDS.put("check", "examine");
        ALL_COMMAND_WORDS.put("ex", "examine");

        // Inventory command variants
        ALL_COMMAND_WORDS.put("inventory", "inventory");
        ALL_COMMAND_WORDS.put("inv", "inventory");
        ALL_COMMAND_WORDS.put("bag", "inventory");
        // Note: "i" is handled as a special case in parseAndExecute

        // Stats command variants
        ALL_COMMAND_WORDS.put("stats", "stats");
        ALL_COMMAND_WORDS.put("stat", "stats");
        ALL_COMMAND_WORDS.put("status", "stats");
        ALL_COMMAND_WORDS.put("info", "stats");

        // Help command variants
        ALL_COMMAND_WORDS.put("help", "help");
        ALL_COMMAND_WORDS.put("h", "help");
        ALL_COMMAND_WORDS.put("?", "help");

        // Use command variants
        ALL_COMMAND_WORDS.put("use", "use");
        ALL_COMMAND_WORDS.put("u", "use");
        ALL_COMMAND_WORDS.put("consume", "use");
        ALL_COMMAND_WORDS.put("drink", "use");
        ALL_COMMAND_WORDS.put("eat", "use");
        ALL_COMMAND_WORDS.put("place", "use");
        ALL_COMMAND_WORDS.put("insert", "use");
        ALL_COMMAND_WORDS.put("put", "use");

        // Go command variants
        ALL_COMMAND_WORDS.put("go", "go");
        ALL_COMMAND_WORDS.put("move", "go");
        ALL_COMMAND_WORDS.put("walk", "go");
        ALL_COMMAND_WORDS.put("head", "go");
        ALL_COMMAND_WORDS.put("travel", "go");

        // Look command variants
        ALL_COMMAND_WORDS.put("look", "look");
        ALL_COMMAND_WORDS.put("l", "look");
        ALL_COMMAND_WORDS.put("see", "look");
        ALL_COMMAND_WORDS.put("observe", "look");

        // Buy command variants
        ALL_COMMAND_WORDS.put("buy", "buy");
        ALL_COMMAND_WORDS.put("purchase", "buy");
        ALL_COMMAND_WORDS.put("acquire", "buy");

        // Sell command variants
        ALL_COMMAND_WORDS.put("sell", "sell");
        ALL_COMMAND_WORDS.put("trade", "sell");
        ALL_COMMAND_WORDS.put("exchange", "sell");
    }

    // Words that indicate direction/movement without explicit "go"
    private static final Set<String> DIRECTION_WORDS = new HashSet<>(Arrays.asList(
            "north", "south", "east", "west", "up", "down", "in", "out",
            "back", "forward", "left", "right", "shop", "store", "market",
            "inn", "tavern", "home", "house", "castle", "forest", "town",
            "city", "village", "exit", "entrance", "door", "gate"
    ));

    public CommandParser(Game game) {
        this.game = game;
        this.commands = new HashMap<>();
        initializeCommands();
    }

    private void initializeCommands() {
        registerCommand("help", new HelpCommand());
        registerCommand("look", new LookCommand());
        registerCommand("stats", new StatsCommand());
        registerCommand("go", new GoCommand());
        registerCommand("inventory", new InventoryCommand());
        registerCommand("use", new UseCommand());
        registerCommand("take", new TakeCommand());
        registerCommand("examine", new ExamineCommand());
        registerCommand("buy", new BuyCommand());
        registerCommand("sell", new SellCommand());
    }

    private void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);

        // Register aliases
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public void parseAndExecute(String input) {
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        String[] words = input.trim().split("\\s+");
        if (words.length == 0) {
            return;
        }

        // Special case: if the only word is "I", open inventory
        if (words.length == 1 && words[0].equalsIgnoreCase("I")) {
            executeCommand("inventory", new String[0], new String[0]);
            return;
        }

        // Find the command in the sentence
        ParsedCommand parsedCommand = findCommandInSentence(words);

        if (parsedCommand == null) {
            game.getGui().displayMessage("I don't understand that command.");
            game.getGui().displayMessage("Type 'help' for available commands.");
            return;
        }

        executeCommand(parsedCommand.commandName, parsedCommand.originalArgs, parsedCommand.filteredArgs);
    }

    private ParsedCommand findCommandInSentence(String[] words) {
        // Strategy 1: Look for explicit command words in the sentence
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            if (ALL_COMMAND_WORDS.containsKey(word)) {
                return createParsedCommand(ALL_COMMAND_WORDS.get(word), words, i);
            }
        }

        // Strategy 2: Check for implicit movement commands
        ParsedCommand movementCommand = checkForImplicitMovement(words);
        if (movementCommand != null) {
            return movementCommand;
        }

        // Strategy 3: Check for context-based commands
        ParsedCommand contextCommand = checkForContextualCommands(words);
        if (contextCommand != null) {
            return contextCommand;
        }

        // Strategy 4: Fallback to first word if it's a valid command
        String firstWord = words[0].toLowerCase();
        if (ALL_COMMAND_WORDS.containsKey(firstWord)) {
            return createParsedCommand(ALL_COMMAND_WORDS.get(firstWord), words, 0);
        }

        return null;
    }

    private ParsedCommand createParsedCommand(String commandName, String[] words, int commandIndex) {
        // Create arguments by excluding the command word
        List<String> argsList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (i != commandIndex) {
                argsList.add(words[i]);
            }
        }

        String[] originalArgs = argsList.toArray(new String[0]);
        String[] filteredArgs = filterArguments(originalArgs);

        return new ParsedCommand(commandName, originalArgs, filteredArgs);
    }

    private ParsedCommand checkForImplicitMovement(String[] words) {
        // Check if any word is a direction without explicit "go"
        for (String word : words) {
            String lowerWord = word.toLowerCase();
            if (DIRECTION_WORDS.contains(lowerWord)) {
                // This is likely a movement command
                String[] filteredArgs = filterArguments(words);
                return new ParsedCommand("go", words, filteredArgs);
            }
        }
        return null;
    }

    private ParsedCommand checkForContextualCommands(String[] words) {
        String sentence = String.join(" ", words).toLowerCase();

        // Check for common patterns
        if (sentence.contains("what") && (sentence.contains("have") || sentence.contains("carrying"))) {
            return new ParsedCommand("inventory", new String[0], new String[0]);
        }

        if (sentence.contains("where") && sentence.contains("am")) {
            return new ParsedCommand("look", new String[0], new String[0]);
        }

        if (sentence.contains("how") && sentence.contains("health")) {
            return new ParsedCommand("stats", new String[0], new String[0]);
        }

        // Check for shop-related patterns
        if (sentence.contains("what") && (sentence.contains("sell") || sentence.contains("buy") || sentence.contains("available"))) {
            return new ParsedCommand("buy", new String[0], new String[0]);
        }

        // Check for implied actions with objects
        if (containsAnyOf(sentence, "potion", "coin", "compass", "sword", "bullet")) {
            if (containsAnyOf(sentence, "drink", "consume", "swallow")) {
                return extractObjectCommand("use", words);
            }
            if (containsAnyOf(sentence, "details", "closer", "closely")) {
                return extractObjectCommand("examine", words);
            }
        }

        return null;
    }

    private boolean containsAnyOf(String sentence, String... keywords) {
        for (String keyword : keywords) {
            if (sentence.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private ParsedCommand extractObjectCommand(String command, String[] words) {
        // Find the object being referenced
        List<String> objectWords = new ArrayList<>();
        for (String word : words) {
            String lowerWord = word.toLowerCase();
            if (!isFillerWord(lowerWord) && !isActionWord(lowerWord)) {
                objectWords.add(word);
            }
        }

        String[] originalArgs = objectWords.toArray(new String[0]);
        String[] filteredArgs = filterArguments(originalArgs);
        return new ParsedCommand(command, originalArgs, filteredArgs);
    }

    private boolean isActionWord(String word) {
        return word.equals("drink") || word.equals("consume") || word.equals("swallow") ||
                word.equals("details") || word.equals("closer") || word.equals("closely") ||
                word.equals("what") || word.equals("where") || word.equals("how") ||
                word.equals("have") || word.equals("carrying") || word.equals("am") ||
                word.equals("health") || word.equals("sell") || word.equals("buy") ||
                word.equals("available");
    }

    private String[] filterArguments(String[] args) {
        List<String> filteredWords = new ArrayList<>();

        for (String word : args) {
            String lowerWord = word.toLowerCase();
            if (!isFillerWord(lowerWord)) {
                filteredWords.add(lowerWord);
            }
        }

        return filteredWords.toArray(new String[0]);
    }

    private boolean isFillerWord(String word) {
        return FILLER_WORDS.contains(word) ||
                word.equals("damn") || word.equals("fucking") || word.equals("bloody") ||
                word.equals("stupid") || word.equals("goddamn") || word.equals("freaking") ||
                word.equals("cursed") || word.equals("blasted");
    }

    private void executeCommand(String commandName, String[] originalArgs, String[] filteredArgs) {
        Command command = commands.get(commandName);
        if (command != null) {
            try {
                if (command instanceof EnhancedCommand) {
                    ((EnhancedCommand) command).execute(game, originalArgs, filteredArgs);
                } else {
                    command.execute(game, filteredArgs);
                }
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

    // Helper class to store parsed command information
    private static class ParsedCommand {
        final String commandName;
        final String[] originalArgs;
        final String[] filteredArgs;

        ParsedCommand(String commandName, String[] originalArgs, String[] filteredArgs) {
            this.commandName = commandName;
            this.originalArgs = originalArgs;
            this.filteredArgs = filteredArgs;
        }
    }

    // Interface for commands that want to handle both original and filtered arguments
    public interface EnhancedCommand extends Command {
        void execute(Game game, String[] originalArgs, String[] filteredArgs);
    }
}