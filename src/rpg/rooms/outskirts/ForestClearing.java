package rpg.rooms.outskirts;

import rpg.core.Game;
import rpg.rooms.Room;

public class ForestClearing extends Room {

    public ForestClearing() {
        super("Forest Clearing", "A small clearing bathed in eerie moonlight.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the clearing");
        game.getGui().displayMessage("You noticed the ground is a bit odd");
        game.getGui().displayMessage("Due to the moonlight you uncover some tracks beneath you");
        displayConnections(game);
    }

}
