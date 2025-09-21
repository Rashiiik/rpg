package rpg.rooms.outskirts;

import rpg.core.Game;
import rpg.rooms.Room;

public class ForestPath extends Room {

    public ForestPath() {
        super("Forest Path", "A winding path through dense forest. Something feels wrong here.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the forest path");
        game.getGui().displayMessage("You see various avaricious trees.");
        game.getGui().displayMessage("Winds Howling");
        displayConnections(game);
    }
}
