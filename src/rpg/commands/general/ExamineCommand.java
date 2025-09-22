package rpg.commands.general;

import rpg.commands.Command;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.rooms.outskirts.Forest;
import rpg.rooms.outskirts.RuinedGlade;
import rpg.rooms.town.Inn;
import rpg.rooms.town.InnArchives;
import rpg.rooms.town.Town;
import rpg.rooms.tutorial.StartingAlley;
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

        Item item = ItemSearchEngine.findInInventoryProgressive(player, target, filteredTarget);
        if (item != null) {
            displayItemExamination(game, item);
            return;
        }

        if (currentRoom instanceof Shop) {
            Shop shop = (Shop) currentRoom;
            if (shop.handleExamine(game, target)) {
                return;
            }
        }

        // TODO: Add similar checks for other rooms that might have custom examination
        if (currentRoom instanceof StartingAlley) {
            StartingAlley startingAlley = (StartingAlley) currentRoom;
            if (startingAlley.handleExamine(game, target)) {
                return;
            }
        }

        if (currentRoom instanceof Town) {
            Town town = (Town) currentRoom;
            if (town.handleExamine(game, target)) {
                return;
            }
        }

        if (currentRoom instanceof Inn) {
            Inn inn = (Inn) currentRoom;
            if (inn.handleExamine(game, target)) {
                return;
            }
        }

        if (currentRoom instanceof Forest) {
            Forest forest = (Forest) currentRoom;
            if (forest.handleExamine(game, target)) {
                return;
            }
        }

        if (currentRoom instanceof InnArchives) {
            InnArchives innArchives = (InnArchives) currentRoom;
            if (innArchives.handleExamine(game, target)) {
                return;
            }
        }

        if (currentRoom instanceof RuinedGlade) {
            RuinedGlade ruinedGlade = (RuinedGlade) currentRoom;
            if (ruinedGlade.handleExamine(game, target)) {
                return;
            }
        }

        Item roomItem = ItemSearchEngine.findInRoomProgressive(currentRoom, target, filteredTarget);
        if (roomItem != null) {
            displayItemExamination(game, roomItem);
            return;
        }

        game.getGui().displayMessage("You look closely but don't find anything called '" + target + "' to examine.");
    }

    private void displayItemExamination(Game game, Item item) {
        game.getGui().displayMessage("=== " + item.getName() + " ===");
        game.getGui().displayMessage(item.getDescription());
        game.getGui().displayMessage("Value: " + item.getValue() + " gold");

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