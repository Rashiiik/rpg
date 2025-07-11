package rpg.rooms.town;

import rpg.rooms.Room;
import rpg.core.Game;
import rpg.items.OldCoin;

public class Shop extends Room {

    public Shop() {
        super("Item Shop", "Tucked away in a fog-drenched alley of the Backlund docks—where gas lamps flicker and shadows stretch with minds of their own—stands a narrow, leaning storefront with windows dulled by age and secrets.");

        // Add an old coin somewhere in the shop initially
        addItem(new OldCoin());
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("A friendly shopkeeper greets you with a warm smile.");
        game.getGui().displayMessage("'Welcome to my shop! Feel free to browse my wares.'");

        // Add puzzle hint on first visit
        if (!game.getStoryFlags().hasFlag("shop_visited")) {
            game.getGui().displayMessage("You notice a peculiar display case behind the counter, gleaming despite the shop's dusty atmosphere.");
            game.getStoryFlags().addFlag("shop_visited");
        }
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the shop.");
        game.getGui().displayMessage("The shelves are lined with:");
        game.getGui().displayMessage("- Food");
        game.getGui().displayMessage("- Books and Magazines");
        game.getGui().displayMessage("- Clothes");
        game.getGui().displayMessage("- Various tools and supplies");

        // Add puzzle elements description
        game.getGui().displayMessage("");
        game.getGui().displayMessage("Behind the counter, you notice:");
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("- An open display case with velvet lining");
            game.getGui().displayMessage("- A wooden counter with a coin-shaped indentation");
        } else {
            game.getGui().displayMessage("- A locked glass display case that pulses with a quiet hum");
            game.getGui().displayMessage("- A worn wooden counter");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("The shopkeeper stands behind the counter, ready to help.");
        game.getGui().displayMessage("");
        displayConnections(game);

        // Show items in the room
        game.getGui().displayMessage("");
        displayItems(game);
    }
}