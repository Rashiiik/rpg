package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;

public class Inn extends Room {

    public Inn() {
        super("The Weary Traveler Inn", "A cozy inn with a warm fireplace and comfortable beds.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("The innkeeper looks up from cleaning a mug and nods at you.");
        game.getGui().displayMessage("'Welcome, traveler! Rest your weary bones here.'");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the inn.");
        game.getGui().displayMessage("The common room is filled with:");
        game.getGui().displayMessage("- Wooden tables and chairs");
        game.getGui().displayMessage("- A crackling fireplace");
        game.getGui().displayMessage("- A few other travelers enjoying meals");
        game.getGui().displayMessage("- A staircase leading to the rooms upstairs");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The innkeeper stands behind the bar, ready to serve.");
        game.getGui().displayMessage("");
        displayConnections(game);
    }
}