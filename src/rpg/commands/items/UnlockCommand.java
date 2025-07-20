package rpg.commands.items;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.player.Player;
import rpg.items.Item;
import rpg.items.Key;
import rpg.rooms.Room;
import rpg.utils.ItemSearchEngine;
import rpg.utils.StringUtils;

public class UnlockCommand implements Command, CommandParser.EnhancedCommand {

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        Player player = game.getPlayer();

        if (originalArgs.length == 0) {
            game.getGui().displayMessage("Unlock what? Please specify what you want to unlock.");
            game.getGui().displayMessage("Usage: unlock <target> or unlock <target> with <key>");
            return;
        }

        // Check if this is "unlock X with Y" command
        int withIndex = StringUtils.findKeywordIndex(originalArgs, "with");
        if (withIndex != -1) {
            executeUnlockWith(game, originalArgs, withIndex, player);
            return;
        }

        // Simple unlock command - try to find a key automatically
        String targetName = StringUtils.buildStringFromArgs(originalArgs);
        executeAutoUnlock(game, targetName, player);
    }

    private void executeUnlockWith(Game game, String[] args, int withIndex, Player player) {
        if (withIndex == 0) {
            game.getGui().displayMessage("You need to specify what to unlock.");
            return;
        }

        if (withIndex == args.length - 1) {
            game.getGui().displayMessage("You need to specify what key to use after 'with'.");
            return;
        }

        String targetName = StringUtils.buildStringFromArgs(args, 0, withIndex);
        String keyName = StringUtils.buildStringFromArgs(args, withIndex + 1, args.length);

        // Find the key in inventory using the centralized search engine
        Item keyItem = ItemSearchEngine.findInInventory(player, keyName);

        if (keyItem == null) {
            game.getGui().displayMessage("You don't have a " + keyName + " in your inventory.");
            return;
        }

        if (!(keyItem instanceof Key)) {
            game.getGui().displayMessage("The " + keyItem.getName() + " is not a key.");
            return;
        }

        Key key = (Key) keyItem;

        // Try to use the key on the target in the current room
        boolean success = tryUseKeyOnTarget(game, player, key, targetName);

        if (!success) {
            game.getGui().displayMessage("The " + key.getName() + " doesn't seem to work on " + targetName + ".");
        }
    }

    private void executeAutoUnlock(Game game, String targetName, Player player) {
        // Try to find any key in the player's inventory using the centralized search approach
        Key foundKey = null;
        for (Item item : player.getInventory().getItems()) {
            if (item instanceof Key) {
                foundKey = (Key) item;
                break;
            }
        }

        if (foundKey == null) {
            game.getGui().displayMessage("You don't have any keys to unlock " + targetName + " with.");
            return;
        }

        // Try to use the found key on the target
        game.getGui().displayMessage("You try to unlock " + targetName + " with your " + foundKey.getName() + ".");

        boolean success = tryUseKeyOnTarget(game, player, foundKey, targetName);

        if (!success) {
            game.getGui().displayMessage("The " + foundKey.getName() + " doesn't seem to work on " + targetName + ".");
        }
    }

    /**
     * Attempts to use a key on a target in the current room.
     * This method handles the interaction between the key and the room.
     */
    private boolean tryUseKeyOnTarget(Game game, Player player, Key key, String targetName) {
        // FIXED: Use Game.getCurrentRoom() instead of RoomManager.getCurrentRoom()
        Room currentRoom = game.getCurrentRoom();

        // Add debug information to see what room we're actually in
        System.out.println("DEBUG - Current room: " + (currentRoom != null ? currentRoom.getClass().getSimpleName() + " - " + currentRoom.getName() : "null"));

        // FIXED: Use the same approach as UseCommand - call handleUseItemOn directly
        if (currentRoom != null) {
            System.out.println("DEBUG - Trying room's handleUseItemOn method directly");
            boolean handled = currentRoom.handleUseItemOn(game, player, key, targetName);
            System.out.println("DEBUG - Room handled: " + handled);

            if (handled) {
                return true;
            }
        }

        // Fallback: Call the key's useOn method directly (same as UseCommand)
        try {
            System.out.println("DEBUG - Trying key's useOn method as fallback");
            key.useOn(player, targetName, game);
            return true;
        } catch (Exception e) {
            System.err.println("Error using key: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String getHelpText() {
        return "Unlock a door or container with a key";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"unlock", "open"};
    }
}