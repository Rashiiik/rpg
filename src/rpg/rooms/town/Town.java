package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;

public class Town extends Room {

    public Town() {
        super("Town Square", "A bustling town square with merchants and travelers.");
        initializeConnections();
    }

    private void initializeConnections() {
        // We'll add connections when other rooms are created
        // For now, we'll add them in the Game class or a RoomManager
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
}