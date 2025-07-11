package rpg.commands.items;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.items.OldCoin;

public class UseOnCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length < 3 || !args[1].equalsIgnoreCase("on")) {
            game.getGui().displayMessage("Usage: use <item> on <target>");
            return;
        }

        Player player = game.getPlayer();
        String itemName = args[0];
        String targetName = args[2];

        // Find the item in player's inventory
        Item item = player.findItem(itemName);
        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + itemName + "'.");
            return;
        }

        // Handle specific puzzle interactions
        if (item instanceof OldCoin && targetName.equalsIgnoreCase("counter")) {
            handleCoinOnCounter(game, player, item);
        } else {
            game.getGui().displayMessage("You can't use the " + itemName + " on " + targetName + ".");
        }
    }

    private void handleCoinOnCounter(Game game, Player player, Item coin) {
        // Check if player is in the shop
        if (!game.getCurrentRoom().getName().equals("Item Shop")) {
            game.getGui().displayMessage("There's no suitable counter here.");
            return;
        }

        // Check if display case is already opened
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case is already open.");
            return;
        }

        // Execute the puzzle solution
        game.getGui().displayMessage("You place the old coin into the circular indentation on the counter.");
        game.getGui().displayMessage("The coin fits perfectly, and you hear a soft *click*.");
        game.getGui().displayMessage("The glass display case opens silently, revealing its contents.");
        game.getGui().displayMessage("A silver compass that ticks mysteriously is now accessible.");

        // Remove the coin (it's consumed in the puzzle)
        player.removeItem(coin);

        // Set the story flag
        game.getStoryFlags().addFlag("opened_display_case");

        // Add the compass to the room so it can be taken
        game.getCurrentRoom().addItem(new rpg.items.SilverCompass());
    }

    @Override
    public String getHelpText() {
        return "Use an item on a target (e.g., 'use coin on counter')";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"place", "insert", "put"};
    }
}