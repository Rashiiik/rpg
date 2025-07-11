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
        int onIndex = findOnKeyword(args);
        if (onIndex != -1) {
            executeUseOn(game, args, onIndex);
            return;
        }

        // Regular use command logic
        executeRegularUse(game, args);
    }

    private int findOnKeyword(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("on")) {
                return i;
            }
        }
        return -1;
    }

    private void executeRegularUse(Game game, String[] args) {
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

        String itemName = buildStringFromArgs(args, 0, onIndex);
        String targetName = buildStringFromArgs(args, onIndex + 1, args.length);

        Player player = game.getPlayer();
        Item item = player.findItem(itemName);

        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + itemName + "'.");
            return;
        }

        // Handle specific puzzle interactions
        if (item instanceof OldCoin && isCounterTarget(targetName)) {
            handleCoinOnCounter(game, player, item);
        } else {
            game.getGui().displayMessage("You can't use the " + itemName + " on " + targetName + ".");
        }
    }

    private String buildStringFromArgs(String[] args, int start, int end) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i > start) builder.append(" ");
            builder.append(args[i]);
        }
        return builder.toString();
    }

    private boolean isCounterTarget(String targetName) {
        return targetName.equalsIgnoreCase("counter") ||
                targetName.equalsIgnoreCase("wooden counter") ||
                targetName.equalsIgnoreCase("indentation") ||
                targetName.equalsIgnoreCase("indent");
    }

    private void handleCoinOnCounter(Game game, Player player, Item coin) {
        if (!game.getCurrentRoom().getName().equals("Item Shop")) {
            game.getGui().displayMessage("There's no suitable counter here.");
            return;
        }

        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case is already open.");
            return;
        }

        // Execute the puzzle solution
        game.getGui().displayMessage("You place the old coin into the circular indentation on the counter.");
        game.getGui().displayMessage("The coin fits perfectly, and you hear a soft *click*.");
        game.getGui().displayMessage("The glass display case opens silently, revealing its contents.");
        game.getGui().displayMessage("A magnificent golden revolver with gleaming silver bullets is now accessible.");

        // Remove the coin and set story flag
        player.removeItem(coin);
        game.getStoryFlags().addFlag("opened_display_case");
        game.getCurrentRoom().addItem(new rpg.items.GoldenRevolver());
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