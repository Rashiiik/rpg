package rpg.commands.items;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;

public class TakeCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Take what? Specify an item to take.");
            return;
        }

        Player player = game.getPlayer();
        String itemName = String.join(" ", args);

        // Check if item is in the current room
        Item item = game.getCurrentRoom().findItem(itemName);
        if (item == null) {
            game.getGui().displayMessage("There's no " + itemName + " here.");
            return;
        }

        // Check if inventory is full
        if (player.getInventory().isFull()) {
            game.getGui().displayMessage("Your inventory is full. You need to drop something first.");
            return;
        }

        // Take the item
        game.getCurrentRoom().removeItem(item);
        player.addItem(item);
        game.getGui().displayMessage("You take the " + item.getName() + ".");
    }

    @Override
    public String getHelpText() {
        return "Take an item from the current location";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"get", "pick", "pickup", "grab"};
    }
}