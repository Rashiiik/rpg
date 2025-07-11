package rpg.commands.shop;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.rooms.town.Shop;
import rpg.shop.Transaction;
import rpg.shop.TransactionType;
import rpg.items.Item;

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

        if (filteredArgs.length == 0) {
            game.getGui().displayMessage("What would you like to sell?");
            game.getGui().displayMessage("Type 'inventory' to see what you have.");
            return;
        }

        String itemName = String.join(" ", filteredArgs);
        int quantity = 1;

        // Check if quantity is specified
        if (filteredArgs.length > 1) {
            try {
                quantity = Integer.parseInt(filteredArgs[filteredArgs.length - 1]);
                if (quantity <= 0) {
                    game.getGui().displayMessage("Invalid quantity. Please specify a positive number.");
                    return;
                }
                String[] itemWords = new String[filteredArgs.length - 1];
                System.arraycopy(filteredArgs, 0, itemWords, 0, itemWords.length);
                itemName = String.join(" ", itemWords);
            } catch (NumberFormatException e) {
                quantity = 1;
                itemName = String.join(" ", filteredArgs);
            }
        }

        Item item = game.getPlayer().getInventory().findItem(itemName);
        if (item == null) {
            game.getGui().displayMessage("You don't have '" + itemName + "'.");
            return;
        }

        int playerItemCount = game.getPlayer().getInventory().getItemCount(item.getName());
        if (playerItemCount < quantity) {
            game.getGui().displayMessage("You only have " + playerItemCount + " of '" + item.getName() + "'.");
            return;
        }

        int sellPrice = (int) (item.getValue() * 0.5); // Sell for half the original value
        int totalValue = sellPrice * quantity;

        // Create and execute transaction with explicit price
        Transaction transaction = new Transaction(TransactionType.SELL, item, quantity, totalValue, game.getPlayer());

        if (transaction.execute()) {
            // Remove the sold items from player's inventory
            if (quantity > 1) {
                game.getPlayer().getInventory().removeItems(item.getName(), quantity);
            } else {
                game.getPlayer().getInventory().removeItem(item);
            }

            // Add the sold items to the shop's inventory
            shop.addToShopInventory(item, quantity);

            String itemText = quantity == 1 ? item.getName() : quantity + " " + item.getName() + "s";
            game.getGui().displayMessage("You sold " + itemText + " for " + totalValue + " gold.");
            game.getGui().displayMessage("You now have " + game.getPlayer().getGold() + " gold.");
        } else {
            game.getGui().displayMessage("Transaction failed. Please try again.");
        }
    }

    @Override
    public String[] getAliases() {
        return new String[]{"trade", "exchange"};
    }

    @Override
    public String getHelpText() {
        return "Sell items to the shop\nUsage: sell <item name> [quantity]\nAliases: trade, exchange";
    }
}