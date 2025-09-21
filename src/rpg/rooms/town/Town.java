package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;

public class Town extends Room {

    public Town() {
        super("Town Square", "A bustling town square with merchants and travelers.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You are in the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("You see paths leading to various locations.");
        game.getGui().displayMessage("Type 'look' to see available exits.");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the town square.");
        game.getGui().displayMessage("There are several buildings here:");
        game.getGui().displayMessage("- A shop to the north");
        game.getGui().displayMessage("- An inn to the east");
        game.getGui().displayMessage("- The town hall to the west");
        game.getGui().displayMessage("- A path to the forest to the south");
        game.getGui().displayMessage("");
        displayConnections(game);
    }

    public boolean handleExamine(Game game, String target) {
        String lowerTarget = target.toLowerCase();

        if (lowerTarget.contains("people") || lowerTarget.contains("merchants") || lowerTarget.contains("travellers")) {
            examinePeople(game);
            return true;
        }

        if (lowerTarget.contains("fountain")) {
            examineFountain(game);
            return true;
        }

        if (lowerTarget.contains("buildings")) {
            examineBuildings(game);
            return true;
        }

        return false;
    }

    public void examinePeople(Game game) {
        game.getGui().displayMessage("The townspeople seem ordinary, but none acknowledge your sudden appearance.");
    }

    public void examineFountain(Game game) {
        game.getGui().displayMessage("A stone fountain sits in the center, water trickling peacefully.");
    }

    public void examineBuildings(Game game) {
        game.getGui().displayMessage("Various shops and buildings surround the square.");
    }
}