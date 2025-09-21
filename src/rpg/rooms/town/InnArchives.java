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

    public boolean handleExamine(Game game, String target) {
        String lowerTarget = target.toLowerCase();

        if (lowerTarget.contains("shelves")) {
            game.getGui().displayMessage("Packed with documents spanning decades, many with pages mysteriously torn out.");
            return true;
        }

        if (lowerTarget.contains("documents")) {
            game.getGui().displayMessage("Records of strange disappearances, reality distortions, and cover-ups.");
            return true;
        }

        if (lowerTarget.contains("maps")) {
            game.getGui().displayMessage("Old town maps showing buildings and streets that no longer exist.");
            return true;
        }

        if (lowerTarget.contains("tunnel")) {
            game.getGui().displayMessage("A hidden passage leading deeper underground.");
            return true;
        }

        return false;
    }

}
