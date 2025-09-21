package rpg.rooms.outskirts;

import rpg.core.Game;
import rpg.rooms.Room;

public class ForestShrine extends Room {

    public ForestShrine() {
        super("Forest Shrine", "An ancient stone shrine covered in glowing runes. Strange creatures lurk in the shadows.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter a circular clearing dominated by an ancient shrine.");
        game.getGui().displayMessage(description);
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the shrine");
        game.getGui().displayMessage("You see some shadowy creatures moving at the edge of your vision");
        game.getGui().displayMessage("There does seem to a protective barrier around the shrine");
        game.getGui().displayMessage("Some runes are scattered on the ground");
        displayConnections(game);
    }
}
