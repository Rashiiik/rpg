package rpg.commands.movement;

import rpg.commands.Command;
import rpg.core.Game;

public class LookCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length > 0) {
            // Look at specific things (implement later)
            String target = String.join(" ", args);
            game.getGui().displayMessage("You look at " + target + ".");
            game.getGui().displayMessage("You don't see anything special about it.");
        } else {
            // Look around the current room
            game.getCurrentRoom().look(game);
        }
    }

    @Override
    public String getHelpText() {
        return "Look around the current area or examine something specific";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"l", "examine", "ex"};
    }
}