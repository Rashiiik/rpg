package rpg.commands.movement;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.rooms.Room;

public class GoCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        // Fallback for backward compatibility
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (originalArgs.length == 0) {
            game.getGui().displayMessage("Go where? Please specify a direction or location.");
            game.getGui().displayMessage("Example: 'go north' or 'go shop'");
            return;
        }

        // Try to find the destination using progressive search
        String destination = findDestination(game, originalArgs, filteredArgs);

        if (destination == null) {
            game.getGui().displayMessage("You can't go there from here.");
            game.getGui().displayMessage("Type 'look' to see available paths.");
            return;
        }

        Room currentRoom = game.getCurrentRoom();
        Room targetRoom = currentRoom.getConnectedRoom(destination);

        if (targetRoom != null) {
            game.setCurrentRoom(targetRoom);
            targetRoom.enter(game);
        } else {
            game.getGui().displayMessage("You can't go " + destination + " from here.");
            game.getGui().displayMessage("Type 'look' to see available paths.");
        }
    }

    private String findDestination(Game game, String[] originalArgs, String[] filteredArgs) {
        Room currentRoom = game.getCurrentRoom();

        // First try the original arguments joined together (exact match)
        if (originalArgs.length > 0) {
            String originalDestination = String.join(" ", originalArgs).toLowerCase();
            if (currentRoom.getConnectedRoom(originalDestination) != null) {
                return originalDestination;
            }
        }

        // Then try the filtered arguments joined together
        if (filteredArgs.length > 0) {
            String filteredDestination = String.join(" ", filteredArgs).toLowerCase();
            if (currentRoom.getConnectedRoom(filteredDestination) != null) {
                return filteredDestination;
            }
        }

        // Try removing emphasis/filler words but keeping descriptive words
        String cleanedDestination = removeEmphasisWords(String.join(" ", originalArgs));
        if (!cleanedDestination.isEmpty() && !cleanedDestination.equals(String.join(" ", originalArgs))) {
            if (currentRoom.getConnectedRoom(cleanedDestination.toLowerCase()) != null) {
                return cleanedDestination.toLowerCase();
            }
        }

        // Try individual words from original args, skipping filler words
        for (String word : originalArgs) {
            String cleanWord = word.toLowerCase();
            if (!isFillerWord(cleanWord)) {
                if (currentRoom.getConnectedRoom(cleanWord) != null) {
                    return cleanWord;
                }
            }
        }

        // Try individual words from filtered args
        for (String word : filteredArgs) {
            String cleanWord = word.toLowerCase();
            if (currentRoom.getConnectedRoom(cleanWord) != null) {
                return cleanWord;
            }
        }

        return null;
    }

    private String removeEmphasisWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            String cleanWord = word.toLowerCase();
            // Remove emphasis words but keep descriptive words
            if (!isEmphasisWord(cleanWord)) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(word);
            }
        }

        return result.toString().trim();
    }

    private boolean isEmphasisWord(String word) {
        return word.equals("damn") || word.equals("fucking") || word.equals("bloody") ||
                word.equals("stupid") || word.equals("goddamn") || word.equals("freaking") ||
                word.equals("cursed") || word.equals("blasted");
    }

    private boolean isValidDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            return false;
        }

        String dest = destination.toLowerCase().trim();

        // Common direction words
        if (dest.equals("north") || dest.equals("south") || dest.equals("east") || dest.equals("west") ||
                dest.equals("up") || dest.equals("down") || dest.equals("in") || dest.equals("out") ||
                dest.equals("back") || dest.equals("forward") || dest.equals("left") || dest.equals("right")) {
            return true;
        }

        // Common location words
        if (dest.equals("shop") || dest.equals("store") || dest.equals("market") || dest.equals("inn") ||
                dest.equals("tavern") || dest.equals("home") || dest.equals("house") || dest.equals("castle") ||
                dest.equals("forest") || dest.equals("town") || dest.equals("city") || dest.equals("village") ||
                dest.equals("exit") || dest.equals("entrance") || dest.equals("door") || dest.equals("gate")) {
            return true;
        }

        // If it's not a common filler word and has some length, consider it valid
        return dest.length() > 2 && !isFillerWord(dest);
    }

    private boolean isFillerWord(String word) {
        return word.equals("i") || word.equals("want") || word.equals("to") || word.equals("the") ||
                word.equals("a") || word.equals("an") || word.equals("and") || word.equals("or") ||
                word.equals("but") || word.equals("at") || word.equals("by") || word.equals("for") ||
                word.equals("with") || word.equals("from") || word.equals("damn") || word.equals("fucking") ||
                word.equals("please") || word.equals("can") || word.equals("could") || word.equals("would") ||
                word.equals("should") || word.equals("will") || word.equals("shall");
    }

    @Override
    public String getHelpText() {
        return "go <direction> - Move to a different location (e.g., 'go north', 'go shop')";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"move", "walk", "travel"};
    }
}