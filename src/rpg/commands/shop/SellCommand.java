package rpg.commands.shop;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.rooms.town.Shop;
import rpg.shop.Transaction;
import rpg.shop.TransactionType;
import rpg.items.Item;
import rpg.utils.ItemSearchEngine;
import rpg.utils.StringUtils;

public class SellCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (!(game.getCurrentRoom() instanceof Shop)) {
            game.getGui().displayMessage("You can only sell items in a shop.");
            return;
        }

        Shop shop = (Shop) game.getCurrentRoom();

        if (originalArgs.length == 0) {
            game.getGui().displayMessage("What would you like to sell?");
            game.getGui().displayMessage("Type 'inventory' to see what you have.");
            return;
        }

        // Use progressive matching strategy
        ParsedSellArgs parsedArgs = parseSellArguments(originalArgs, filteredArgs);

        if (parsedArgs.itemName.isEmpty()) {
            game.getGui().displayMessage("What would you like to sell?");
            return;
        }

        // Try multiple search strategies
        Item item = findItemToSell(game, parsedArgs.itemName, originalArgs);
        if (item == null) {
            game.getGui().displayMessage("You don't have '" + parsedArgs.itemName + "'.");
            return;
        }

        int playerItemCount = game.getPlayer().getInventory().getItemCount(item.getName());
        if (playerItemCount < parsedArgs.quantity) {
            game.getGui().displayMessage("You only have " + playerItemCount + " of '" + item.getName() + "'.");
            return;
        }

        int sellPrice = (int) (item.getValue() * 0.5); // Sell for half the original value
        int totalValue = sellPrice * parsedArgs.quantity;

        // Create and execute transaction with explicit price
        Transaction transaction = new Transaction(TransactionType.SELL, item, parsedArgs.quantity, totalValue, game.getPlayer());

        if (transaction.execute()) {
            // Transaction already removes items from inventory, so we don't need to do it again

            // Add the sold items to the shop's inventory
            shop.addToShopInventory(item, parsedArgs.quantity);

            String itemText = parsedArgs.quantity == 1 ? item.getName() : parsedArgs.quantity + " " + item.getName() + "s";
            game.getGui().displayMessage("You sold " + itemText + " for " + totalValue + " gold.");
            game.getGui().displayMessage("You now have " + game.getPlayer().getGold() + " gold.");
        } else {
            game.getGui().displayMessage("Transaction failed. Please try again.");
        }
    }

    private ParsedSellArgs parseSellArguments(String[] originalArgs, String[] filteredArgs) {
        String itemName = "";
        int quantity = 1;

        if (originalArgs.length > 0) {
            String lastArg = originalArgs[originalArgs.length - 1];
            if (StringUtils.isNumber(lastArg)) {
                quantity = Integer.parseInt(lastArg);
                if (quantity <= 0) {
                    quantity = 1;
                }
                itemName = StringUtils.buildStringFromArgs(originalArgs, 0, originalArgs.length - 1);
            } else {
                itemName = StringUtils.buildStringFromArgs(originalArgs);
            }
        }

        if (itemName.trim().isEmpty() && filteredArgs.length > 0) {
            itemName = StringUtils.buildStringFromArgs(filteredArgs);
        }

        return new ParsedSellArgs(itemName.trim(), quantity);
    }

    private Item findItemToSell(Game game, String itemName, String[] originalArgs) {
        String filteredItemName = ItemSearchEngine.filterSearchTerm(itemName);
        Item item = ItemSearchEngine.findInInventoryProgressive(game.getPlayer(), itemName, filteredItemName);
        if (item != null) return item;

        for (String word : originalArgs) {
            if (!StringUtils.isNumber(word)) {
                item = ItemSearchEngine.findInInventory(game.getPlayer(), word);
                if (item != null) return item;
            }
        }

        for (int i = 0; i < originalArgs.length; i++) {
            for (int j = i + 1; j <= originalArgs.length; j++) {
                String combination = StringUtils.buildStringFromArgs(originalArgs, i, j);
                if (!StringUtils.isNumber(combination)) {
                    item = ItemSearchEngine.findInInventory(game.getPlayer(), combination);
                    if (item != null) return item;
                }
            }
        }

        return null;
    }

    private static class ParsedSellArgs {
        final String itemName;
        final int quantity;

        ParsedSellArgs(String itemName, int quantity) {
            this.itemName = itemName;
            this.quantity = quantity;
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"trade", "exchange"};
    }

    @Override
    public String getHelpText() {
        return "Sell items to the shop";
    }
}