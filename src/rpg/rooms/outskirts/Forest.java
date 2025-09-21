package rpg.rooms.outskirts;

import rpg.rooms.Room;
import rpg.core.Game;

public class Forest extends Room {

    public Forest() {
        super("Dark Forest", "A dense forest with towering trees that block out most of the sunlight.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("The air is thick with the scent of moss and damp earth.");
        game.getGui().displayMessage("You hear strange sounds echoing through the trees...");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the dark forest.");
        game.getGui().displayMessage("The forest contains:");
        game.getGui().displayMessage("- Tall, ancient trees");
        game.getGui().displayMessage("- Thick undergrowth");
        game.getGui().displayMessage("- Mysterious shadows");
        game.getGui().displayMessage("- A narrow path winding deeper into the forest");
        game.getGui().displayMessage("- Strange glowing mushrooms");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("You sense danger lurking in the shadows.");
        game.getGui().displayMessage("");
        displayConnections(game);
    }

    public boolean handleExamine(Game game, String target) {
        String lowerTarget = target.toLowerCase();

        if (lowerTarget.contains("tree") || lowerTarget.contains("trees")) {
            game.getGui().displayMessage("Massive, ancient trees that seem to whisper in the wind.");
        }

        if (lowerTarget.contains("path")) {
            game.getGui().displayMessage("A winding path disappears into the forest depths.");
        }

        return false;
    }


}