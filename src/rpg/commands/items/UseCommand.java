package rpg.commands.items;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;

public class UseCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Use what? Specify an item name.");
            game.getGui().displayMessage("Usage: use <item_name>");
            return;
        }

        Player player = game.getPlayer();
        String itemName = String.join(" ", args); // Join all args in case item name has spaces

        Item item = player.findItem(itemName);
        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + itemName + "'.");
            return;
        }

        // Capture the use output
        try {
            // Store old HP to show healing effects
            int oldHp = player.getHp();

            // Use the item
            item.use(player);

            // If it was a healing item, show the effect in the GUI
            if (player.getHp() > oldHp) {
                int healedAmount = player.getHp() - oldHp;
                game.getGui().displayMessage("You use the " + item.getName() + " and restore " + healedAmount + " HP.");
            } else {
                game.getGui().displayMessage("You use the " + item.getName() + ".");
            }

            // If the item was consumable and used, it should be removed
            if (item.isConsumable() && !player.hasItem(item.getName())) {
                game.getGui().displayMessage("The " + item.getName() + " is consumed.");
            }

        } catch (Exception e) {
            game.getGui().displayMessage("You can't use the " + item.getName() + " right now.");
        }
    }

    @Override
    public String getHelpText() {
        return "Use an item from your inventory";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"u", "consume", "drink", "eat"};
    }
}