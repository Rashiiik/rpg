package rpg.commands.general;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;

public class ExamineCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Examine what? Specify something to examine.");
            return;
        }

        String target = String.join(" ", args).toLowerCase();

        // Handle room-specific examinations
        if (game.getCurrentRoom().getName().equals("Item Shop")) {
            handleShopExamination(game, target);
        } else {
            // Check if it's an item in inventory
            Player player = game.getPlayer();
            Item item = player.findItem(target);
            if (item != null) {
                game.getGui().displayMessage("=== " + item.getName() + " ===");
                game.getGui().displayMessage(item.getDescription());
                game.getGui().displayMessage("Value: " + item.getValue() + " gold");
            } else {
                game.getGui().displayMessage("You don't see anything special about " + target + ".");
            }
        }
    }

    private void handleShopExamination(Game game, String target) {
        switch (target) {
            case "counter":
                if (game.getStoryFlags().hasFlag("opened_display_case")) {
                    game.getGui().displayMessage("The wooden counter has a small circular indentation where you placed the old coin.");
                    game.getGui().displayMessage("The display case behind it is now open.");
                } else {
                    game.getGui().displayMessage("The wooden counter is worn smooth from years of use.");
                    game.getGui().displayMessage("You notice a small circular indentation carved into the surface, just the size of a coin.");
                }
                break;

            case "display case":
            case "case":
                if (game.getStoryFlags().hasFlag("opened_display_case")) {
                    game.getGui().displayMessage("The glass display case is now open, its contents accessible.");
                } else {
                    game.getGui().displayMessage("A velvet-lined display case gleams behind the counter, untouched by dust.");
                    game.getGui().displayMessage("Whatever is inside pulses with a quiet hum.");
                    game.getGui().displayMessage("It appears to be locked, but you see no visible keyhole.");
                }
                break;

            default:
                game.getGui().displayMessage("You don't see anything special about " + target + ".");
        }
    }

    @Override
    public String getHelpText() {
        return "Examine objects, items, or areas closely";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"ex", "inspect", "study"};
    }
}