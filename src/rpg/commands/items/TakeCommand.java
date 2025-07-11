package rpg.commands.items;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.utils.ItemUtil;

public class TakeCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        // Fallback for backward compatibility
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (originalArgs.length == 0) {
            game.getGui().displayMessage("Take what? Specify an item to take.");
            return;
        }

        Player player = game.getPlayer();

        // Create search terms from both original and filtered arguments
        String originalTarget = String.join(" ", originalArgs);
        String filteredTarget = String.join(" ", filteredArgs);

        // Check if item is in the current room using progressive search
        Item item = ItemUtil.findItemInRoom(game.getCurrentRoom(), originalTarget, filteredTarget);

        if (item == null) {
            // Try some additional search strategies
            item = tryAlternativeSearch(game, originalTarget, filteredTarget);
        }

        if (item == null) {
            game.getGui().displayMessage("There's no \"" + originalTarget + "\" here.");

            // Provide helpful suggestions if possible
            suggestSimilarItems(game, originalTarget);
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

        // Provide context-aware feedback
        if (ItemUtil.containsEmphasisWords(originalTarget)) {
            game.getGui().displayMessage("You grab the " + item.getName() + " with determination.");
        } else {
            game.getGui().displayMessage("You take the " + item.getName() + ".");
        }
    }

    private Item tryAlternativeSearch(Game game, String originalTarget, String filteredTarget) {
        // Try removing emphasis words but keeping descriptive words
        String cleanedTarget = ItemUtil.removeEmphasisWords(originalTarget);
        if (!cleanedTarget.equals(originalTarget) && !cleanedTarget.isEmpty()) {
            Item item = game.getCurrentRoom().findItem(cleanedTarget);
            if (item != null) {
                return item;
            }
        }

        // Try individual words from the search term
        String[] words = originalTarget.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && !ItemUtil.containsEmphasisWords(word)) {
                Item item = game.getCurrentRoom().findItem(word);
                if (item != null) {
                    return item;
                }
            }
        }

        // Try the existing room's findItem method with filtered target
        if (!filteredTarget.equals(originalTarget)) {
            return game.getCurrentRoom().findItem(filteredTarget);
        }

        return null;
    }

    private void suggestSimilarItems(Game game, String searchTerm) {
        // Get all items in the room
        var roomItems = game.getCurrentRoom().getItems();

        if (roomItems.isEmpty()) {
            game.getGui().displayMessage("There are no items here to take.");
            return;
        }

        // Find items that might be similar
        String[] searchWords = searchTerm.toLowerCase().split("\\s+");
        for (Item roomItem : roomItems) {
            String itemName = roomItem.getName().toLowerCase();

            // Check if any search word appears in the item name
            for (String searchWord : searchWords) {
                if (itemName.contains(searchWord) && searchWord.length() > 2) {
                    game.getGui().displayMessage("Did you mean the " + roomItem.getName() + "?");
                    return;
                }
            }
        }

        // If no similar items found, just list what's available
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