package rpg.commands.general;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class ExamineCommand implements Command {

    private static final Set<String> ARTICLES = new HashSet<>(Arrays.asList(
            "a", "an", "the"
    ));

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Examine what? Specify something to examine closely.");
            return;
        }

        String target = filterAndJoinArgs(args);

        Player player = game.getPlayer();
        Item item = findItemInInventory(player, target);
        if (item != null) {
            game.getGui().displayMessage("=== " + item.getName() + " ===");
            game.getGui().displayMessage(item.getDescription());
            game.getGui().displayMessage("Value: " + item.getValue() + " gold");

            provideDetailedItemInfo(game, item, target);
            return;
        }

        if (game.getCurrentRoom().getName().equals("Item Shop")) {
            if (handleDetailedShopExamination(game, target)) {
                return;
            }
        }

        Item roomItem = game.getCurrentRoom().findItem(target);
        if (roomItem != null) {
            game.getGui().displayMessage("=== " + roomItem.getName() + " ===");
            game.getGui().displayMessage(roomItem.getDescription());
            game.getGui().displayMessage("Value: " + roomItem.getValue() + " gold");
            return;
        }

        // Nothing found
        game.getGui().displayMessage("You look closely but don't find anything called '" + target + "' to examine.");
    }

    private void provideDetailedItemInfo(Game game, Item item, String target) {
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

    private String filterAndJoinArgs(String[] args) {
        StringBuilder result = new StringBuilder();

        for (String arg : args) {
            String lowerArg = arg.toLowerCase();
            if (!ARTICLES.contains(lowerArg)) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(lowerArg);
            }
        }

        return result.toString();
    }

    private Item findItemInInventory(Player player, String target) {
        Item item = player.findItem(target);
        if (item != null) {
            return item;
        }

        for (Item inventoryItem : player.getInventory().getItems()) {
            if (inventoryItem.matchesSearch(target)) {
                return inventoryItem;
            }
        }

        return null;
    }

    private boolean handleDetailedShopExamination(Game game, String target) {
        if (target.contains("counter") || target.contains("indentation") || target.contains("indent")) {
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
            return true;
        }

        if (target.contains("display") || target.contains("case") || target.contains("glass")) {
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
            return true;
        }

        // Shopkeeper - detailed examination reveals more personality
        if (target.contains("shopkeeper") || target.contains("keeper") || target.contains("merchant")) {
            game.getGui().displayMessage("You study the shopkeeper carefully.");
            game.getGui().displayMessage("Their eyes hold ancient wisdom, far older than their appearance suggests.");
            game.getGui().displayMessage("Small scars on their hands tell of a life beyond shopkeeping.");
            game.getGui().displayMessage("When they think you're not looking, they glance meaningfully at the display case.");
            return true;
        }

        // Shelves - detailed examination reveals organization
        if (target.contains("shelf") || target.contains("shelves")) {
            game.getGui().displayMessage("You examine the shelves more closely.");
            game.getGui().displayMessage("Everything is organized by more than just type - there's a pattern here.");
            game.getGui().displayMessage("Magical items are subtly separated from mundane ones.");
            game.getGui().displayMessage("Some items seem to be placed deliberately to hide others behind them.");
            return true;
        }

        // Windows - new detailed examination option
        if (target.contains("window") || target.contains("windows")) {
            game.getGui().displayMessage("You peer through the grimy windows.");
            game.getGui().displayMessage("The dirt seems intentionally placed to obscure the view.");
            game.getGui().displayMessage("You can just make out shadowy figures moving in the street outside.");
            game.getGui().displayMessage("This shop exists in a place between worlds...");
            return true;
        }

        return false;
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