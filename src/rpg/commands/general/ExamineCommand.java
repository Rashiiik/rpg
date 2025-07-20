package rpg.commands.general;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.utils.ItemSearchEngine;
import rpg.utils.StringUtils;
import rpg.rooms.Room;
import rpg.rooms.town.Shop;

public class ExamineCommand implements Command {

    @Override
    public void execute(Game game, String[] args) {
        if (args.length == 0) {
            game.getGui().displayMessage("Examine what? Specify something to examine closely.");
            return;
        }

        String target = StringUtils.buildStringFromArgs(args);
        String filteredTarget = ItemSearchEngine.filterSearchTerm(target);
        Player player = game.getPlayer();
        Room currentRoom = game.getCurrentRoom();

        // Check inventory first
        Item item = ItemSearchEngine.findInInventoryProgressive(player, target, filteredTarget);
        if (item != null) {
            displayItemExamination(game, item);
            return;
        }

        // Check if current room has custom examination handling
        if (currentRoom instanceof Shop) {
            Shop shop = (Shop) currentRoom;
            if (shop.handleExamine(game, target)) {
                return;
            }
        }

        // TODO: Add similar checks for other rooms that might have custom examination
        // if (currentRoom instanceof SomeOtherRoom) {
        //     SomeOtherRoom otherRoom = (SomeOtherRoom) currentRoom;
        //     if (otherRoom.handleExamine(game, target)) {
        //         return;
        //     }
        // }

        // Check room items
        Item roomItem = ItemSearchEngine.findInRoomProgressive(currentRoom, target, filteredTarget);
        if (roomItem != null) {
            displayItemExamination(game, roomItem);
            return;
        }

        // Nothing found
        game.getGui().displayMessage("You look closely but don't find anything called '" + target + "' to examine.");
    }

    private void displayItemExamination(Game game, Item item) {
        // Basic item info
        game.getGui().displayMessage("=== " + item.getName() + " ===");
        game.getGui().displayMessage(item.getDescription());
        game.getGui().displayMessage("Value: " + item.getValue() + " gold");

        // Let the item provide detailed examination if it has any
        item.examine(game);
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