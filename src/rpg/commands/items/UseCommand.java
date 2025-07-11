package rpg.commands.items;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.items.OldCoin;

public class UseCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Use what? Specify an item name.");
            game.getGui().displayMessage("Usage: use <item_name> or use <item> on <target>");
            return;
        }

        // Check if this is a "use X on Y" command
        boolean hasOnKeyword = false;
        int onIndex = -1;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("on")) {
                hasOnKeyword = true;
                onIndex = i;
                break;
            }
        }

        if (hasOnKeyword) {
            // Delegate to UseOnCommand logic
            executeUseOn(game, args, onIndex);
            return;
        }

        // Regular use command logic
        Player player = game.getPlayer();
        String itemName = String.join(" ", args);

        Item item = player.findItem(itemName);
        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + itemName + "'.");
            return;
        }

        try {
            int oldHp = player.getHp();
            item.use(player);

            if (player.getHp() > oldHp) {
                int healedAmount = player.getHp() - oldHp;
                game.getGui().displayMessage("You use the " + item.getName() + " and restore " + healedAmount + " HP.");
            } else {
                game.getGui().displayMessage("You use the " + item.getName() + ".");
            }

            if (item.isConsumable() && !player.hasItem(item.getName())) {
                game.getGui().displayMessage("The " + item.getName() + " is consumed.");
            }

        } catch (Exception e) {
            game.getGui().displayMessage("You can't use the " + item.getName() + " right now.");
        }
    }

    private void executeUseOn(Game game, String[] args, int onIndex) {
        if (onIndex == 0) {
            game.getGui().displayMessage("You need to specify an item to use.");
            return;
        }

        if (onIndex == args.length - 1) {
            game.getGui().displayMessage("You need to specify a target after 'on'.");
            return;
        }

        // Build item name from args before "on"
        StringBuilder itemNameBuilder = new StringBuilder();
        for (int i = 0; i < onIndex; i++) {
            if (i > 0) itemNameBuilder.append(" ");
            itemNameBuilder.append(args[i]);
        }
        String itemName = itemNameBuilder.toString();

        // Build target name from args after "on"
        StringBuilder targetNameBuilder = new StringBuilder();
        for (int i = onIndex + 1; i < args.length; i++) {
            if (i > onIndex + 1) targetNameBuilder.append(" ");
            targetNameBuilder.append(args[i]);
        }
        String targetName = targetNameBuilder.toString();

        Player player = game.getPlayer();

        // Find the item in player's inventory
        Item item = player.findItem(itemName);
        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + itemName + "'.");
            return;
        }

        // Handle specific puzzle interactions
        if (item instanceof OldCoin && (targetName.equalsIgnoreCase("counter") || targetName.equalsIgnoreCase("wooden counter"))) {
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
        return "Use an item from your inventory, or use an item on a target";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"u", "consume", "drink", "eat"};
    }
}