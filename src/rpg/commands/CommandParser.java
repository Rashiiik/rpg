package rpg.commands;

import rpg.commands.general.ExamineCommand;
import rpg.commands.items.TakeCommand;
import rpg.commands.items.UseOnCommand;
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

    // Natural language processing lists
    private static final Set<String> ARTICLES = new HashSet<>(Arrays.asList(
            "a", "an", "the"
    ));

    private static final Set<String> PREPOSITIONS = new HashSet<>(Arrays.asList(
            "to", "in", "at", "by", "for", "with", "from", "into", "onto", "upon"
    ));

    private static final Set<String> FILLER_WORDS = new HashSet<>(Arrays.asList(
            "i", "want", "would", "like", "please", "can", "could", "should", "will", "shall"
    ));

    // Verb synonyms mapping
    private static final Map<String, String> VERB_SYNONYMS = new HashMap<>();
    static {
        VERB_SYNONYMS.put("use", "use");
        VERB_SYNONYMS.put("utilize", "use");
        VERB_SYNONYMS.put("consume", "use");
        VERB_SYNONYMS.put("drink", "use");
        VERB_SYNONYMS.put("eat", "use");

        VERB_SYNONYMS.put("take", "take");
        VERB_SYNONYMS.put("get", "take");
        VERB_SYNONYMS.put("grab", "take");
        VERB_SYNONYMS.put("pick", "take");
        VERB_SYNONYMS.put("pickup", "take");

        VERB_SYNONYMS.put("look", "look");
        VERB_SYNONYMS.put("see", "look");
        VERB_SYNONYMS.put("observe", "look");
        VERB_SYNONYMS.put("view", "look");

        VERB_SYNONYMS.put("examine", "examine");
        VERB_SYNONYMS.put("inspect", "examine");
        VERB_SYNONYMS.put("check", "examine");
        VERB_SYNONYMS.put("study", "examine");

        VERB_SYNONYMS.put("go", "go");
        VERB_SYNONYMS.put("move", "go");
        VERB_SYNONYMS.put("walk", "go");
        VERB_SYNONYMS.put("head", "go");
        VERB_SYNONYMS.put("travel", "go");

        VERB_SYNONYMS.put("inventory", "inventory");
        VERB_SYNONYMS.put("items", "inventory");
        VERB_SYNONYMS.put("bag", "inventory");
        VERB_SYNONYMS.put("backpack", "inventory");

        VERB_SYNONYMS.put("stats", "stats");
        VERB_SYNONYMS.put("status", "stats");
        VERB_SYNONYMS.put("health", "stats");

        VERB_SYNONYMS.put("help", "help");
        VERB_SYNONYMS.put("commands", "help");
        VERB_SYNONYMS.put("assistance", "help");

        // Add UseOnCommand verb synonyms
        VERB_SYNONYMS.put("place", "useon");
        VERB_SYNONYMS.put("insert", "useon");
        VERB_SYNONYMS.put("put", "useon");
    }

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
        registerCommand("useon", new UseOnCommand());
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

        // Try natural language parsing first
        ParsedCommand parsed = parseNaturalLanguage(input);
        if (parsed != null) {
            executeCommand(parsed.verb, parsed.arguments);
            return;
        }

        // Fall back to traditional parsing
        String[] parts = input.trim().toLowerCase().split("\\s+");
        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);
        executeCommand(commandName, args);
    }

    private ParsedCommand parseNaturalLanguage(String input) {
        String[] words = input.trim().toLowerCase().split("\\s+");

        // Find the main verb
        String verb = null;
        int verbIndex = -1;

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (VERB_SYNONYMS.containsKey(word)) {
                verb = VERB_SYNONYMS.get(word);
                verbIndex = i;
                break;
            }
        }

        if (verb == null) {
            return null; // No recognized verb found
        }

        // Extract meaningful words after the verb
        List<String> meaningfulWords = new ArrayList<>();
        boolean foundOn = false;

        for (int i = verbIndex + 1; i < words.length; i++) {
            String word = words[i];

            // Skip filler words
            if (FILLER_WORDS.contains(word)) {
                continue;
            }

            // Keep "on" for use-on commands
            if (word.equals("on")) {
                foundOn = true;
                meaningfulWords.add(word);
                continue;
            }

            // For "go" command, skip "to" preposition but keep other words
            if (verb.equals("go") && word.equals("to")) {
                continue;
            }

            // Skip other prepositions except "on"
            if (PREPOSITIONS.contains(word) && !word.equals("on")) {
                continue;
            }

            // Skip articles unless they're part of location names or important context
            if (ARTICLES.contains(word)) {
                // For most cases, just skip articles entirely
                // This is simpler and more reliable than trying to determine importance
                continue;
            }

            // Keep all other words
            meaningfulWords.add(word);
        }

        // Handle special cases
        if ((verb.equals("use") && foundOn) || verb.equals("useon")) {
            // Route to UseOnCommand
            return new ParsedCommand("useon", meaningfulWords.toArray(new String[0]));
        }

        // Handle direction extraction for "go" command
        if (verb.equals("go")) {
            meaningfulWords = extractDirections(meaningfulWords);
        }

        return new ParsedCommand(verb, meaningfulWords.toArray(new String[0]));
    }

    private boolean isImportantNoun(String word) {
        // Common location and item words that should keep their articles
        Set<String> importantNouns = new HashSet<>(Arrays.asList(
                "shop", "counter", "coin", "compass", "door", "chest", "table",
                "room", "cave", "forest", "house", "store", "market", "inn",
                "potion", "sword", "shield", "armor", "key", "map", "book"
        ));
        return importantNouns.contains(word);
    }

    private List<String> extractDirections(List<String> words) {
        // Common direction words and their mappings
        Map<String, String> directionMap = new HashMap<>();
        directionMap.put("north", "north");
        directionMap.put("south", "south");
        directionMap.put("east", "east");
        directionMap.put("west", "west");
        directionMap.put("up", "up");
        directionMap.put("down", "down");
        directionMap.put("northeast", "northeast");
        directionMap.put("northwest", "northwest");
        directionMap.put("southeast", "southeast");
        directionMap.put("southwest", "southwest");

        // Common location names that should be treated as destinations
        Set<String> locationNames = new HashSet<>(Arrays.asList(
                "shop", "store", "market", "inn", "tavern", "house", "home", "cave",
                "forest", "town", "city", "village", "room", "chamber", "hall"
        ));

        List<String> result = new ArrayList<>();

        // First check for traditional directions
        for (String word : words) {
            if (directionMap.containsKey(word)) {
                result.add(directionMap.get(word));
            }
        }

        // If no directions found, check for location names
        if (result.isEmpty()) {
            for (String word : words) {
                if (locationNames.contains(word)) {
                    result.add(word);
                }
            }
        }

        // If still no matches, return the original words
        return result.isEmpty() ? words : result;
    }

    private void executeCommand(String commandName, String[] args) {
        // Special handling for "use X on Y" syntax
        if (commandName.equals("use") && containsOnKeyword(args)) {
            Command useOnCommand = commands.get("useon");
            if (useOnCommand != null) {
                try {
                    useOnCommand.execute(game, args);
                } catch (Exception e) {
                    game.getGui().displayMessage("Error executing command: " + e.getMessage());
                }
                return;
            }
        }

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
            suggestSimilarCommands(commandName);
        }
    }

    private boolean containsOnKeyword(String[] args) {
        for (String arg : args) {
            if (arg.equalsIgnoreCase("on")) {
                return true;
            }
        }
        return false;
    }

    private void suggestSimilarCommands(String input) {
        // Simple suggestion system
        List<String> suggestions = new ArrayList<>();

        for (String verb : VERB_SYNONYMS.keySet()) {
            if (verb.startsWith(input.substring(0, Math.min(input.length(), 2)))) {
                suggestions.add(VERB_SYNONYMS.get(verb));
            }
        }

        if (!suggestions.isEmpty()) {
            game.getGui().displayMessage("Did you mean: " + String.join(", ", suggestions));
        }
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void addCommand(String name, Command command) {
        registerCommand(name, command);
    }

    // Helper class to hold parsed command data
    private static class ParsedCommand {
        public final String verb;
        public final String[] arguments;

        public ParsedCommand(String verb, String[] arguments) {
            this.verb = verb;
            this.arguments = arguments;
        }
    }
}