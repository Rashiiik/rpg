package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;
import rpg.items.Item;
import rpg.items.OldCoin;

public class ShopBasement extends Room {

    public ShopBasement() {
        super("Shop Basement", "A dimly lit basement beneath the shop. The air is thick with dust and the scent of old parchment. Ancient shelves line the walls, filled with forgotten relics and mysterious artifacts.");

        // Add some special items that can only be found in the basement
        addItem(new OldCoin());
        addItem(new OldCoin());
        addItem(new OldCoin()); // Multiple old coins as treasure
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You descend into the " + name);
        game.getGui().displayMessage(description);

        if (!game.getStoryFlags().hasFlag("basement_visited")) {
            game.getGui().displayMessage("The wooden stairs creak under your feet as you make your way down.");
            game.getGui().displayMessage("You feel a chill in the air and notice the walls are carved from stone.");
            game.getGui().displayMessage("This place feels much older than the shop above.");
            game.getStoryFlags().addFlag("basement_visited");
        }

        game.getGui().displayMessage("The basement is filled with shadows and secrets.");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You examine the basement carefully.");
        game.getGui().displayMessage("The stone walls are covered in strange symbols and faded murals.");
        game.getGui().displayMessage("Ancient wooden shelves contain:");
        game.getGui().displayMessage("- Dusty tomes and scrolls");
        game.getGui().displayMessage("- Peculiar artifacts wrapped in cloth");
        game.getGui().displayMessage("- Glass bottles filled with unknown substances");
        game.getGui().displayMessage("- Several ornate chests (locked)");

        game.getGui().displayMessage("");
        game.getGui().displayMessage("In the corner, you notice a small shrine with flickering candles.");

        if (game.getStoryFlags().hasFlag("basement_secret_found")) {
            game.getGui().displayMessage("The secret passage you discovered earlier is still open.");
        } else {
            game.getGui().displayMessage("Something about the eastern wall seems different...");
        }

        game.getGui().displayMessage("");
        displayConnections(game);

        // Show items in the basement
        game.getGui().displayMessage("");
        displayItems(game);
    }
}