package rpg.commands.items;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.items.Key;
import rpg.utils.ItemSearchEngine;
import rpg.utils.StringUtils;

public class UseCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (originalArgs.length == 0) {
            game.getGui().displayMessage("Use what? Specify an item name.");
            game.getGui().displayMessage("Usage: use <item_name> or use <item> on <target>");
            return;
        }

        // Check if this is a "use X on Y" command
        int onIndex = StringUtils.findKeywordIndex(originalArgs, "on");
        if (onIndex != -1) {
            executeUseOn(game, originalArgs, onIndex);
            return;
        }

        // Regular use command logic
        executeRegularUse(game, originalArgs);
    }

    private void executeRegularUse(Game game, String[] args) {
        Player player = game.getPlayer();
        String itemName = StringUtils.cleanInput(StringUtils.buildStringFromArgs(args));

        // Use centralized search engine
        Item item = ItemSearchEngine.findInInventory(player, itemName);

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

        String itemName = StringUtils.cleanInput(StringUtils.buildStringFromArgs(args, 0, onIndex));
        String targetName = StringUtils.cleanInput(StringUtils.buildStringFromArgs(args, onIndex + 1, args.length));

        Player player = game.getPlayer();

        Item item = ItemSearchEngine.findInInventory(player, itemName);

        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + itemName + "'.");
            return;
        }

        if (item instanceof Key) {
            Key key = (Key) item;
            key.useOn(player, targetName, game);
            return;
        }

        if (game.getCurrentRoom().handleUseItemOn(game, player, item, targetName)) {
            return;
        }

        // If room doesn't handle it, show generic failure message
        game.getGui().displayMessage("You can't use the " + itemName + " on " + targetName + ".");
    }

    @Override
    public String getHelpText() {
        return "Use an item from your inventory, or use an item on a target";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"u", "consume", "drink", "eat", "place", "insert", "put"};
    }
}