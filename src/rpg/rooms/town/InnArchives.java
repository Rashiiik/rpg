package rpg.rooms.town;

import rpg.core.Game;
import rpg.rooms.Room;

public class InnArchives extends Room {

    public InnArchives() {
        super("Hidden Archives", "A dusty underground chamber filled with old records and forgotten documents.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("Beneath the inn lies a repository of the town's true history.");
        game.getGui().displayMessage("Shelves line the walls, packed with documents, maps, and records that tell a different story than what the townspeople remember.");
    }

}
