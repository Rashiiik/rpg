package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;

public class Shop extends Room {

    public Shop() {
        super("Item Shop", "A small shop filled with various items and equipment.");
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("A friendly shopkeeper greets you with a warm smile.");
        game.getGui().displayMessage("'Welcome to my shop! Feel free to browse my wares.'");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the shop.");
        game.getGui().displayMessage("The shelves are lined with:");
        game.getGui().displayMessage("- Health potions");
        game.getGui().displayMessage("- Basic weapons");
        game.getGui().displayMessage("- Leather armor");
        game.getGui().displayMessage("- Various tools and supplies");
        game.getGui().displayMessage("");
        game.getGui().displayMessage("The shopkeeper stands behind the counter, ready to help.");
        game.getGui().displayMessage("");
        displayConnections(game);
    }
}