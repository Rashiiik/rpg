package rpg.commands.items;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;

public class InventoryCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        Player player = game.getPlayer();

        if (args.length == 0) {
            displayInventory(game, player);
        } else if (args.length == 1) {
            String action = args[0].toLowerCase();

            switch (action) {
                case "value":
                case "worth":
                    displayInventoryValue(game, player);
                    break;
                case "clear":
                    clearInventory(game, player);
                    break;
                default:
                    showItemDetails(game, player, args[0]);
                    break;
            }
        } else {
            game.getGui().displayMessage("Usage: inventory [item_name|value|clear]");
        }
    }

    private void displayInventory(Game game, Player player) {
        String inventoryDisplay = player.getInventory().getInventoryDisplay();
        game.getGui().displayMessage(inventoryDisplay);
    }

    private void displayInventoryValue(Game game, Player player) {
        int totalValue = player.getInventory().getTotalValue();
        game.getGui().displayMessage("Total inventory value: " + totalValue + " gold");
    }

    private void clearInventory(Game game, Player player) {
        if (player.getInventory().isEmpty()) {
            game.getGui().displayMessage("Your inventory is already empty.");
            return;
        }

        player.getInventory().clear();
        game.getGui().displayMessage("Inventory cleared.");
    }

    private void showItemDetails(Game game, Player player, String itemName) {
        Item item = player.getInventory().findItem(itemName);

        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + itemName + "'.");
            return;
        }

        game.getGui().displayMessage("=== " + item.getName() + " ===");
        game.getGui().displayMessage("Description: " + item.getDescription());
        game.getGui().displayMessage("Value: " + item.getValue() + " gold");
        game.getGui().displayMessage("Consumable: " + (item.isConsumable() ? "Yes" : "No"));
    }

    @Override
    public String getHelpText() {
        return "Display your inventory, check item details, or manage inventory";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"inv", "i", "bag"};
    }
}