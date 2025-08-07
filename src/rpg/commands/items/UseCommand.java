package rpg.commands.items;

import rpg.commands.Command;
import rpg.commands.CommandParser;
import rpg.core.Game;
import rpg.items.OldCoin;
import rpg.player.Player;
import rpg.items.Item;
import rpg.items.Key;
import rpg.rooms.Room;
import rpg.utils.ItemSearchEngine;
import rpg.utils.StringUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UseCommand implements Command, CommandParser.EnhancedCommand {

    private static final Set<String> CONNECTION_WORDS = new HashSet<>(Arrays.asList(
            "on", "with", "into", "in", "against", "to", "at", "upon", "onto"
    ));

    private static final Set<String> UNLOCK_ACTIONS = new HashSet<>(Arrays.asList(
            "unlock", "open", "insert", "put", "place"
    ));

    private static final Set<String> UNLOCK_TARGETS = new HashSet<>(Arrays.asList(
            "door", "chest", "gate", "lock", "basement", "entrance", "exit"
    ));

    @Override
    public void execute(Game game, String[] args) {
        execute(game, args, args);
    }

    @Override
    public void execute(Game game, String[] originalArgs, String[] filteredArgs) {
        if (originalArgs.length == 0) {
            game.getGui().displayMessage("Use what? Specify an item name.");
            game.getGui().displayMessage("Usage: use <item_name> or use <item> on <target>");
            return;
        }

        UsagePattern pattern = analyzeUsagePattern(originalArgs);

        switch (pattern.type) {
            case USE_ON:
                executeUseOn(game, originalArgs, filteredArgs, pattern.connectionIndex);
                break;
            case UNLOCK_TARGET:
                executeUnlockTarget(game, originalArgs, filteredArgs, pattern);
                break;
            case UNLOCK_WITH:
                executeUnlockWith(game, originalArgs, filteredArgs, pattern);
                break;
            case REGULAR_USE:
            default:
                executeRegularUse(game, originalArgs, filteredArgs);
                break;
        }
    }

    private UsagePattern analyzeUsagePattern(String[] args) {
        String input = String.join(" ", args).toLowerCase();

        for (int i = 0; i < args.length; i++) {
            if (CONNECTION_WORDS.contains(args[i].toLowerCase())) {
                return new UsagePattern(PatternType.USE_ON, i);
            }
        }

        boolean hasUnlockAction = false;
        boolean hasUnlockTarget = false;

        for (String arg : args) {
            String lowerArg = arg.toLowerCase();
            if (UNLOCK_ACTIONS.contains(lowerArg)) {
                hasUnlockAction = true;
            }
            if (UNLOCK_TARGETS.contains(lowerArg)) {
                hasUnlockTarget = true;
            }
        }

        if (hasUnlockAction && hasUnlockTarget) {
            return new UsagePattern(PatternType.UNLOCK_TARGET, -1);
        }

        if (hasUnlockTarget && !hasUnlockAction) {
            return new UsagePattern(PatternType.UNLOCK_TARGET, -1);
        }

        return new UsagePattern(PatternType.REGULAR_USE, -1);
    }

    private void executeRegularUse(Game game, String[] originalArgs, String[] filteredArgs) {
        Player player = game.getPlayer();

        Item item = ItemSearchEngine.findInInventoryProgressive(
                player,
                StringUtils.buildStringFromArgs(originalArgs),
                StringUtils.buildStringFromArgs(filteredArgs)
        );

        if (item == null) {
            String searchTerm = StringUtils.buildStringFromArgs(originalArgs);
            game.getGui().displayMessage("You don't have an item called '" + searchTerm + "'.");
            return;
        }

        try {
            int oldHp = player.getHp();
            item.use(player);

            if (player.getHp() > oldHp) {
                int healedAmount = player.getHp() - oldHp;
                game.getGui().displayMessage("You use the " + item.getName() + " and restore " + healedAmount + " HP.");
            } else {
                game.getGui().displayMessage("You use the " + item.getName() + ".");
            }

            if (item.isConsumable() && !player.hasItem(item.getName())) {
                game.getGui().displayMessage("The " + item.getName() + " is consumed.");
            }

        } catch (Exception e) {
            game.getGui().displayMessage("You can't use the " + item.getName() + " right now.");
        }
    }

    private void executeUseOn(Game game, String[] originalArgs, String[] filteredArgs, int onIndex) {
        if (onIndex == 0) {
            game.getGui().displayMessage("You need to specify an item to use.");
            return;
        }

        if (onIndex == originalArgs.length - 1) {
            game.getGui().displayMessage("You need to specify a target after '" + originalArgs[onIndex] + "'.");
            return;
        }

        int filteredOnIndex = findConnectionWordIndex(filteredArgs);

        String originalItemName = StringUtils.buildStringFromArgs(originalArgs, 0, onIndex);
        String filteredItemName = (filteredOnIndex != -1) ?
                StringUtils.buildStringFromArgs(filteredArgs, 0, filteredOnIndex) :
                StringUtils.buildStringFromArgs(filteredArgs, 0, Math.min(filteredArgs.length, onIndex));

        String originalTargetName = StringUtils.buildStringFromArgs(originalArgs, onIndex + 1, originalArgs.length);
        String filteredTargetName = (filteredOnIndex != -1 && filteredOnIndex < filteredArgs.length - 1) ?
                StringUtils.buildStringFromArgs(filteredArgs, filteredOnIndex + 1, filteredArgs.length) :
                originalTargetName;

        executeItemOnTarget(game, originalItemName, filteredItemName, originalTargetName, filteredTargetName);
    }

    private void executeUnlockTarget(Game game, String[] originalArgs, String[] filteredArgs, UsagePattern pattern) {
        Player player = game.getPlayer();

        String target = extractUnlockTarget(originalArgs);
        if (target == null) {
            game.getGui().displayMessage("What do you want to unlock?");
            return;
        }

        Key bestKey = findBestKeyForTarget(player, target);

        if (bestKey == null) {
            game.getGui().displayMessage("You don't have a suitable key to unlock " + target + ".");
            return;
        }

        handleKeyUsage(game, player, bestKey, target, target.toLowerCase());
    }

    private void executeUnlockWith(Game game, String[] originalArgs, String[] filteredArgs, UsagePattern pattern) {
        int withIndex = StringUtils.findKeywordIndex(originalArgs, "with");
        if (withIndex != -1) {
            executeUseOn(game, originalArgs, filteredArgs, withIndex);
        }
    }

    private void executeItemOnTarget(Game game, String originalItemName, String filteredItemName,
                                     String originalTargetName, String filteredTargetName) {
        Player player = game.getPlayer();

        Item item = ItemSearchEngine.findInInventoryProgressive(player, originalItemName, filteredItemName);

        if (item == null) {
            game.getGui().displayMessage("You don't have an item called '" + originalItemName + "'.");
            return;
        }

        if (item instanceof Key) {
            Key key = (Key) item;
            handleKeyUsage(game, player, key, originalTargetName, filteredTargetName);
            return;
        }

        if (item instanceof OldCoin) {
            OldCoin coin = (OldCoin) item;
            coin.useOn(player, filteredTargetName, game);
            return;
        }

        game.getGui().displayMessage("You can't use the " + item.getName() + " on " + originalTargetName + ".");
        game.getGui().displayMessage("Maybe you're not in the right location, or this combination doesn't work.");
    }

    private String extractUnlockTarget(String[] args) {
        for (String arg : args) {
            String lowerArg = arg.toLowerCase();
            if (UNLOCK_TARGETS.contains(lowerArg)) {
                return arg;
            }
        }

        StringBuilder target = new StringBuilder();
        for (String arg : args) {
            if (!UNLOCK_ACTIONS.contains(arg.toLowerCase())) {
                if (target.length() > 0) target.append(" ");
                target.append(arg);
            }
        }

        return target.length() > 0 ? target.toString() : null;
    }

    private Key findBestKeyForTarget(Player player, String target) {
        for (Item item : player.getInventory().getItems()) {
            if (item instanceof Key) {
                Key key = (Key) item;
                return key;
            }
        }
        return null;
    }

    private int findConnectionWordIndex(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (CONNECTION_WORDS.contains(args[i].toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    private void handleKeyUsage(Game game, Player player, Key key, String originalTargetName, String filteredTargetName) {
        Room currentRoom = game.getCurrentRoom();

        if (currentRoom != null) {
            boolean handled = currentRoom.handleUseItemOn(game, player, key, filteredTargetName);

            if (handled) {
                return;
            }

            if (!originalTargetName.equals(filteredTargetName)) {
                handled = currentRoom.handleUseItemOn(game, player, key, originalTargetName);

                if (handled) {
                    return;
                }
            }
        }

        try {
            key.useOn(player, filteredTargetName, game);
        } catch (Exception e) {
            System.err.println("Error using key: " + e.getMessage());
            game.getGui().displayMessage("The " + key.getName() + " doesn't seem to work on " + originalTargetName + ".");
        }
    }

    @Override
    public String getHelpText() {
        return "Use an item from your inventory, or use an item on a target (including unlocking with keys)";
    }

    @Override
    public String[] getAliases() {
        return new String[]{"u", "consume", "drink", "eat", "place", "insert", "put", "unlock", "open"};
    }

    private static class UsagePattern {
        final PatternType type;
        final int connectionIndex;

        UsagePattern(PatternType type, int connectionIndex) {
            this.type = type;
            this.connectionIndex = connectionIndex;
        }
    }

    private enum PatternType {
        REGULAR_USE,
        USE_ON,
        UNLOCK_TARGET,
        UNLOCK_WITH
    }
}