package rpg.commands.general;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.utils.ItemUtil;

public class ExamineCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Examine what? Specify something to examine closely.");
            return;
        }

        String target = ItemUtil.filterAndJoinArgs(args);
        Player player = game.getPlayer();

        // Check inventory first
        Item item = ItemUtil.findItemInInventory(player, target);
        if (item != null) {
            displayItemDetails(game, item);
            provideDetailedItemInfo(game, item);
            return;
        }

        // Check for special shop examinations
        if (game.getCurrentRoom().getName().equals("Item Shop")) {
            if (handleDetailedShopExamination(game, target)) {
                return;
            }
        }

        // Check room items
        Item roomItem = game.getCurrentRoom().findItem(target);
        if (roomItem != null) {
            displayItemDetails(game, roomItem);
            return;
        }

        // Nothing found
        game.getGui().displayMessage("You look closely but don't find anything called '" + target + "' to examine.");
    }

    private void displayItemDetails(Game game, Item item) {
        game.getGui().displayMessage("=== " + item.getName() + " ===");
        game.getGui().displayMessage(item.getDescription());
        game.getGui().displayMessage("Value: " + item.getValue() + " gold");
    }

    private void provideDetailedItemInfo(Game game, Item item) {
        String itemName = item.getName().toLowerCase();

        if (itemName.contains("coin")) {
            game.getGui().displayMessage("The coin is worn smooth with age. Strange symbols are etched around its edge.");
            game.getGui().displayMessage("It feels warm to the touch and seems to vibrate slightly.");
        } else if (itemName.contains("potion")) {
            game.getGui().displayMessage("The liquid inside swirls with its own inner light.");
            game.getGui().displayMessage("You can smell a faint herbal aroma even through the cork.");
        } else if (itemName.contains("compass")) {
            game.getGui().displayMessage("The compass needle spins wildly, as if magnetic north doesn't exist here.");
            game.getGui().displayMessage("Strange runes are carved into the brass casing.");
        } else if (itemName.contains("sword")) {
            game.getGui().displayMessage("The blade is perfectly balanced and shows no signs of wear.");
            game.getGui().displayMessage("Light seems to gather along its edge.");
        }
    }

    private boolean handleDetailedShopExamination(Game game, String target) {
        if (target.contains("counter") || target.contains("indentation") || target.contains("indent")) {
            examineCounter(game);
            return true;
        }

        if (target.contains("display") || target.contains("case") || target.contains("glass")) {
            examineDisplayCase(game);
            return true;
        }

        if (target.contains("shopkeeper") || target.contains("keeper") || target.contains("merchant")) {
            examineShopkeeper(game);
            return true;
        }

        if (target.contains("shelf") || target.contains("shelves")) {
            examineShelves(game);
            return true;
        }

        if (target.contains("window") || target.contains("windows")) {
            examineWindows(game);
            return true;
        }

        return false;
    }

    private void examineCounter(Game game) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("You run your fingers along the counter's surface.");
            game.getGui().displayMessage("The circular indentation is perfectly smooth, as if the coin melted into it.");
            game.getGui().displayMessage("You notice faint magical residue around the edges.");
        } else {
            game.getGui().displayMessage("You examine the counter closely.");
            game.getGui().displayMessage("The circular indentation is precisely carved - definitely not natural wear.");
            game.getGui().displayMessage("Running your finger around its edge, you feel a faint magical tingle.");
            game.getGui().displayMessage("This was made to hold something specific...");
        }
    }

    private void examineDisplayCase(Game game) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case's glass is crystal clear, without a single fingerprint.");
            game.getGui().displayMessage("The opening mechanism is completely hidden - no hinges or locks visible.");
            game.getGui().displayMessage("Whatever magic opened this, it's beyond your understanding.");
        } else {
            game.getGui().displayMessage("You press your face close to the glass.");
            game.getGui().displayMessage("Inside, you can make out the shape of a magical compass.");
            game.getGui().displayMessage("The glass itself seems to shimmer with protective enchantments.");
            game.getGui().displayMessage("No amount of force would break this - it needs to be opened properly.");
        }
    }

    private void examineShopkeeper(Game game) {
        game.getGui().displayMessage("You study the shopkeeper carefully.");
        game.getGui().displayMessage("Their eyes hold ancient wisdom, far older than their appearance suggests.");
        game.getGui().displayMessage("Small scars on their hands tell of a life beyond shopkeeping.");
        game.getGui().displayMessage("When they think you're not looking, they glance meaningfully at the display case.");
    }

    private void examineShelves(Game game) {
        game.getGui().displayMessage("You examine the shelves more closely.");
        game.getGui().displayMessage("Everything is organized by more than just type - there's a pattern here.");
        game.getGui().displayMessage("Magical items are subtly separated from mundane ones.");
        game.getGui().displayMessage("Some items seem to be placed deliberately to hide others behind them.");
    }

    private void examineWindows(Game game) {
        game.getGui().displayMessage("You peer through the grimy windows.");
        game.getGui().displayMessage("The dirt seems intentionally placed to obscure the view.");
        game.getGui().displayMessage("You can just make out shadowy figures moving in the street outside.");
        game.getGui().displayMessage("This shop exists in a place between worlds...");
    }

    @Override
    public String getHelpText() {
        return "Examine objects, items, or areas closely to discover hidden details";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ex", "inspect", "study"};
    }
}