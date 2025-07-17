package rpg.commands;

import rpg.commands.general.ExamineCommand;
import rpg.commands.items.TakeCommand;
import rpg.commands.items.UnlockCommand;
import rpg.commands.shop.BuyCommand;
import rpg.commands.shop.SellCommand;
import rpg.core.Game;
import rpg.commands.general.HelpCommand;
import rpg.commands.general.StatsCommand;
import rpg.commands.movement.LookCommand;
import rpg.commands.movement.GoCommand;
import rpg.commands.items.InventoryCommand;
import rpg.commands.items.UseCommand;
import rpg.utils.StringUtils;
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

    private static final Map<String, String> ALL_COMMAND_WORDS = new HashMap<>();

    // Static initializer to reduce duplication
    static {
        initializeCommandMappings();
    }

    private static void initializeCommandMappings() {
        // Define command groups to reduce duplication
        addCommandGroup("take", "take", "get", "grab", "pickup", "pick");
        addCommandGroup("examine", "examine", "inspect", "study", "check", "ex");
        addCommandGroup("inventory", "inventory", "inv", "bag");
        addCommandGroup("stats", "stats", "stat", "status", "info");
        addCommandGroup("help", "help", "h", "?");
        addCommandGroup("use", "use", "u", "consume", "drink", "eat", "place", "insert", "put");
        addCommandGroup("go", "go", "move", "walk", "head", "travel");
        addCommandGroup("look", "look", "l", "see", "observe");
        addCommandGroup("buy", "buy", "purchase", "acquire");
        addCommandGroup("sell", "sell", "trade", "exchange");
        addCommandGroup("unlock", "unlock", "open"); // Added unlock command
    }

    private static void addCommandGroup(String mainCommand, String... aliases) {
        for (String alias : aliases) {
            ALL_COMMAND_WORDS.put(alias, mainCommand);
        }
    }

    private static final Set<String> DIRECTION_WORDS = new HashSet<>(Arrays.asList(
            "north", "south", "east", "west", "up", "down", "in", "out",
            "back", "forward", "left", "right", "shop", "store", "market",
            "inn", "tavern", "home", "house", "castle", "forest", "town",
            "city", "village", "exit", "entrance", "door", "gate"
    ));

    private static final Set<String> GAME_OBJECTS = new HashSet<>(Arrays.asList(
            "potion", "coin", "coins", "compass", "sword", "bullet", "bullets",
            "key", "keys", "gem", "gems", "ring", "rings", "shield", "armor",
            "bow", "arrow", "arrows", "scroll", "scrolls", "book", "books",
            "torch", "torches", "rope", "food", "bread", "water", "wine",
            "health", "mana", "magic", "spell", "spells", "counter", "table",
            "chest", "door", "altar", "statue", "fountain", "well", "tree",
            "rock", "stone", "wall", "floor", "ceiling", "window", "chair",
            "bed", "barrel", "crate", "box", "lever", "button", "switch",
            "revolver", "pistol", "gun", "weapon", "knife", "blade", "axe",
            "basement", "downstairs" // Added basement-related words
    ));

    private static final Set<String> IMPORTANT_PREPOSITIONS = new HashSet<>(Arrays.asList(
            "on", "in", "into", "onto", "upon", "against", "over", "under",
            "behind", "beside", "near", "inside", "outside", "through", "across",
            "with" // Added "with" for "unlock X with Y" commands
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
        registerCommand("unlock", new UnlockCommand()); // Added unlock command
    }

    private void registerCommand(String name, Command command) {
        commands.put(name.toLowerCase(), command);

        // Register aliases
        for (String alias : command.getAliases()) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public void parseAndExecute(String input) {
        if (StringUtils.isNullOrEmpty(input)) {
            return;
        }

        String[] words = input.trim().split("\\s+");
        if (words.length == 0) {
            return;
        }

        // Special case for single "I" -> inventory
        if (words.length == 1 && words[0].equalsIgnoreCase("I")) {
            executeCommand("inventory", new String[0], new String[0]);
            return;
        }

        ParsedCommand parsedCommand = findCommandInSentence(words);

        if (parsedCommand == null) {
            game.getGui().displayMessage("I don't understand that command.");
            game.getGui().displayMessage("Type 'help' for available commands.");
            return;
        }

        executeCommand(parsedCommand.commandName, parsedCommand.originalArgs, parsedCommand.filteredArgs);
    }

    private ParsedCommand findCommandInSentence(String[] words) {
        // Strategy 1: Look for explicit command words
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            if (ALL_COMMAND_WORDS.containsKey(word)) {
                return createParsedCommand(ALL_COMMAND_WORDS.get(word), words, i);
            }
        }

        // Strategy 2: Check for implicit movement
        ParsedCommand movementCommand = checkForImplicitMovement(words);
        if (movementCommand != null) {
            return movementCommand;
        }

        // Strategy 3: Check for contextual commands
        ParsedCommand contextCommand = checkForContextualCommands(words);
        if (contextCommand != null) {
            return contextCommand;
        }

        // Strategy 4: Default to first word if it's a known command
        String firstWord = words[0].toLowerCase();
        if (ALL_COMMAND_WORDS.containsKey(firstWord)) {
            return createParsedCommand(ALL_COMMAND_WORDS.get(firstWord), words, 0);
        }

        return null;
    }

    private ParsedCommand createParsedCommand(String commandName, String[] words, int commandIndex) {
        // Extract all arguments except the command word itself
        List<String> argsList = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            if (i != commandIndex) {
                argsList.add(words[i]);
            }
        }

        String[] originalArgs = argsList.toArray(new String[0]);

        // Create multiple argument variations for better matching
        String[] filteredArgs = createArgumentVariations(originalArgs, commandName);

        return new ParsedCommand(commandName, originalArgs, filteredArgs);
    }

    private String[] createArgumentVariations(String[] originalArgs, String commandName) {
        if (originalArgs.length == 0) {
            return new String[0];
        }

        List<String> bestArgs = new ArrayList<>();

        for (String arg : originalArgs) {
            String lowerArg = arg.toLowerCase();

            // Keep numbers, game objects, directions, and important prepositions
            if (StringUtils.isNumber(arg) ||
                    GAME_OBJECTS.contains(lowerArg) ||
                    DIRECTION_WORDS.contains(lowerArg) ||
                    IMPORTANT_PREPOSITIONS.contains(lowerArg)) {
                bestArgs.add(lowerArg);
            }
        }

        // If we filtered out everything, use the first original arg
        if (bestArgs.isEmpty() && originalArgs.length > 0) {
            bestArgs.add(originalArgs[0].toLowerCase());
        }

        return bestArgs.toArray(new String[0]);
    }

    private ParsedCommand checkForImplicitMovement(String[] words) {
        for (String word : words) {
            String lowerWord = word.toLowerCase();
            if (DIRECTION_WORDS.contains(lowerWord)) {
                String[] filteredArgs = createArgumentVariations(words, "go");
                return new ParsedCommand("go", words, filteredArgs);
            }
        }
        return null;
    }

    private ParsedCommand checkForContextualCommands(String[] words) {
        String sentence = String.join(" ", words).toLowerCase();

        // Use helper methods to reduce duplication
        if (containsWords(sentence, "what") && containsAnyOf(sentence, "have", "carrying")) {
            return new ParsedCommand("inventory", new String[0], new String[0]);
        }

        if (containsWords(sentence, "where") && containsWords(sentence, "am")) {
            return new ParsedCommand("look", new String[0], new String[0]);
        }

        if (containsWords(sentence, "how") && containsWords(sentence, "health")) {
            return new ParsedCommand("stats", new String[0], new String[0]);
        }

        if (containsWords(sentence, "what") && containsAnyOf(sentence, "sell", "buy", "available")) {
            return new ParsedCommand("buy", new String[0], new String[0]);
        }

        // Check for unlock-related phrases
        if (containsAnyOf(sentence, "unlock", "open") && containsAnyOf(sentence, "door", "basement")) {
            return extractObjectCommand("unlock", words);
        }

        if (containsAnyGameObject(sentence)) {
            if (containsAnyOf(sentence, "drink", "consume", "swallow")) {
                return extractObjectCommand("use", words);
            }
            if (containsAnyOf(sentence, "details", "closer", "closely")) {
                return extractObjectCommand("examine", words);
            }
        }

        return null;
    }

    // Helper methods to reduce duplication
    private boolean containsWords(String sentence, String word) {
        return sentence.contains(word);
    }

    private boolean containsAnyGameObject(String sentence) {
        return GAME_OBJECTS.stream().anyMatch(sentence::contains);
    }

    private boolean containsAnyOf(String sentence, String... keywords) {
        return Arrays.stream(keywords).anyMatch(sentence::contains);
    }

    private ParsedCommand extractObjectCommand(String command, String[] words) {
        String[] originalArgs = words;
        String[] filteredArgs = createArgumentVariations(originalArgs, command);
        return new ParsedCommand(command, originalArgs, filteredArgs);
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

    public interface EnhancedCommand extends Command {
        void execute(Game game, String[] originalArgs, String[] filteredArgs);
    }
}