package rpg.commands.items;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.utils.ItemSearchEngine;
import rpg.utils.StringUtils;

public class TakeCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (originalArgs.length == 0) {
            game.getGui().displayMessage("Take what? Specify an item to take.");
            return;
        }

        Player player = game.getPlayer();

        String originalTarget = StringUtils.buildStringFromArgs(originalArgs);
        String filteredTarget = StringUtils.buildStringFromArgs(filteredArgs);

        Item item = ItemSearchEngine.findInRoomProgressive(
                game.getCurrentRoom(), originalTarget, filteredTarget
        );

        if (item == null) {
            game.getGui().displayMessage("There's no \"" + originalTarget + "\" here.");
            suggestSimilarItems(game, originalTarget);
            return;
        }

        if (player.getInventory().isFull()) {
            game.getGui().displayMessage("Your inventory is full. You need to drop something first.");
            return;
        }

        game.getCurrentRoom().removeItem(item);
        player.addItem(item);

        game.getGui().displayMessage("You take the " + item.getName() + ".");

    }

    private void suggestSimilarItems(Game game, String searchTerm) {
        var roomItems = game.getCurrentRoom().getItems();

        if (roomItems.isEmpty()) {
            game.getGui().displayMessage("There are no items here to take.");
            return;
        }

        String[] searchWords = searchTerm.toLowerCase().split("\\s+");
        for (Item roomItem : roomItems) {
            String itemName = roomItem.getName().toLowerCase();

            for (String searchWord : searchWords) {
                if (itemName.contains(searchWord) && searchWord.length() > 2) {
                    game.getGui().displayMessage("Did you mean the " + roomItem.getName() + "?");
                    return;
                }
            }
        }

        if (roomItems.size() == 1) {
            game.getGui().displayMessage("There is a " + roomItems.get(0).getName() + " here.");
        } else {
            game.getGui().displayMessage("Available items here: " +
                    roomItems.stream()
                            .map(Item::getName)
                            .collect(java.util.stream.Collectors.joining(", ")));
        }
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