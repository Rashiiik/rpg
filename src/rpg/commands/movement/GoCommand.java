package rpg.commands.movement;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.rooms.Room;

public class GoCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Go where? Please specify a direction or location.");
            game.getGui().displayMessage("Example: 'go north' or 'go shop'");
            return;
        }

        String direction = args[0].toLowerCase();
        Room currentRoom = game.getCurrentRoom();
        Room targetRoom = currentRoom.getConnectedRoom(direction);

        if (targetRoom != null) {
            game.setCurrentRoom(targetRoom);
            targetRoom.enter(game);
        } else {
            game.getGui().displayMessage("You can't go " + direction + " from here.");
            game.getGui().displayMessage("Type 'look' to see available paths.");
        }
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