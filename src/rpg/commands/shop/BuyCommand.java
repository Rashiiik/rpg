package rpg.commands.shop;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.rooms.town.Shop;
import rpg.shop.ShopItem;
import rpg.shop.Transaction;
import rpg.shop.TransactionType;
import rpg.items.Item;

public class BuyCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (!(game.getCurrentRoom() instanceof Shop)) {
            game.getGui().displayMessage("You can only buy items in a shop.");
            return;
        }

        Shop shop = (Shop) game.getCurrentRoom();

        if (filteredArgs.length == 0) {
            game.getGui().displayMessage("What would you like to buy?");
            shop.displayShopInventory(game);
            return;
        }

        String itemName = String.join(" ", filteredArgs);
        int quantity = 1;

        // Check if quantity is specified
        if (filteredArgs.length > 1) {
            try {
                // Try to parse the last word as a number
                quantity = Integer.parseInt(filteredArgs[filteredArgs.length - 1]);
                if (quantity <= 0) {
                    game.getGui().displayMessage("Invalid quantity. Please specify a positive number.");
                    return;
                }
                // Remove the quantity from the item name
                String[] itemWords = new String[filteredArgs.length - 1];
                System.arraycopy(filteredArgs, 0, itemWords, 0, itemWords.length);
                itemName = String.join(" ", itemWords);
            } catch (NumberFormatException e) {
                // Last word is not a number, so treat it as part of the item name
                quantity = 1;
                itemName = String.join(" ", filteredArgs);
            }
        }

        ShopItem shopItem = shop.findShopItem(itemName);
        if (shopItem == null) {
            game.getGui().displayMessage("The shop doesn't sell '" + itemName + "'.");
            return;
        }

        if (!shopItem.canBuy()) {
            game.getGui().displayMessage("Sorry, '" + shopItem.getItem().getName() + "' is not for sale.");
            return;
        }

        if (shopItem.getStock() < quantity) {
            game.getGui().displayMessage("Sorry, the shop only has " + shopItem.getStock() +
                    " of '" + shopItem.getItem().getName() + "' in stock.");
            return;
        }

        int totalCost = shopItem.getBuyPrice() * quantity;
        if (game.getPlayer().getGold() < totalCost) {
            game.getGui().displayMessage("You don't have enough gold. You need " + totalCost +
                    " gold but only have " + game.getPlayer().getGold() + ".");
            return;
        }

        // Create and execute transaction with explicit price
        Transaction transaction = new Transaction(TransactionType.BUY, shopItem.getItem(),
                quantity, totalCost, game.getPlayer());

        if (transaction.execute()) {
            shopItem.reduceStock(quantity);
            String itemText = quantity == 1 ? shopItem.getItem().getName() :
                    quantity + " " + shopItem.getItem().getName() + "s";
            game.getGui().displayMessage("You bought " + itemText + " for " + totalCost + " gold.");
            game.getGui().displayMessage("You now have " + game.getPlayer().getGold() + " gold remaining.");
        } else {
            game.getGui().displayMessage("Transaction failed. Please try again.");
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"purchase", "get", "acquire"};
    }

    @Override
    public String getHelpText() {
        return "Buy items from the shop\nUsage: buy <item name> [quantity]\nAliases: purchase, get, acquire";
    }
}