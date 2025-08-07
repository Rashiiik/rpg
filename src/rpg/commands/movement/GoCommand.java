package rpg.commands.movement;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.rooms.Room;
import rpg.utils.StringUtils;

public class GoCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (originalArgs.length == 0) {
            game.getGui().displayMessage("Go where? Please specify a direction or location.");
            game.getGui().displayMessage("Example: 'go north' or 'go shop'");
            return;
        }

        String destination = findDestination(game, originalArgs, filteredArgs);

        if (destination == null) {
            game.getGui().displayMessage("You can't go there from here.");
            game.getGui().displayMessage("Type 'look' to see available paths.");
            return;
        }

        Room currentRoom = game.getCurrentRoom();
        Room targetRoom = currentRoom.tryMove(destination, game);

        if (targetRoom != null) {
            game.setCurrentRoom(targetRoom);
            targetRoom.enter(game);
        }
    }

    private String findDestination(Game game, String[] originalArgs, String[] filteredArgs) {
        Room currentRoom = game.getCurrentRoom();

        if (originalArgs.length > 0) {
            String originalDestination = StringUtils.cleanInput(
                    StringUtils.buildStringFromArgs(originalArgs)
            ).toLowerCase();
            if (!StringUtils.isNullOrEmpty(originalDestination) &&
                    isValidDestination(currentRoom, originalDestination, game)) {
                return originalDestination;
            }
        }

        if (filteredArgs.length > 0) {
            String filteredDestination = StringUtils.cleanInput(
                    StringUtils.buildStringFromArgs(filteredArgs)
            ).toLowerCase();
            if (!StringUtils.isNullOrEmpty(filteredDestination) &&
                    isValidDestination(currentRoom, filteredDestination, game)) {
                return filteredDestination;
            }
        }

        for (String word : originalArgs) {
            String cleanWord = StringUtils.cleanInput(word).toLowerCase();
            if (!StringUtils.isNullOrEmpty(cleanWord) &&
                    isValidDestination(currentRoom, cleanWord, game)) {
                return cleanWord;
            }
        }

        for (String word : filteredArgs) {
            String cleanWord = StringUtils.cleanInput(word).toLowerCase();
            if (!StringUtils.isNullOrEmpty(cleanWord) &&
                    isValidDestination(currentRoom, cleanWord, game)) {
                return cleanWord;
            }
        }

        return null;
    }

    private boolean isValidDestination(Room currentRoom, String destination, Game game) {
        return currentRoom.getConnectedRoom(destination) != null;
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