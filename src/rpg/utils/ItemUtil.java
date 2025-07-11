package rpg.utils;

import rpg.items.Item;
import rpg.player.Player;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class ItemUtil {

    private static final Set<String> ARTICLES = new HashSet<>(Arrays.asList(
            "a", "an", "the"
    ));

    public static String filterAndJoinArgs(String[] args) {
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

    public static Item findItemInInventory(Player player, String target) {
        // Try direct search using the enhanced inventory method
        Item item = player.getInventory().findItem(target);
        if (item != null) {
            return item;
        }

        // Additional fallback - try item's own matching method
        for (Item inventoryItem : player.getInventory().getItems()) {
            if (inventoryItem.matchesSearch(target)) {
                return inventoryItem;
            }
        }

        return null;
    }

    // Enhanced method that tries multiple search strategies
    public static Item findItemInInventoryProgressive(Player player, String originalTarget, String filteredTarget) {
        // Strategy 1: Try original target first
        Item item = findItemInInventory(player, originalTarget);
        if (item != null) {
            return item;
        }

        // Strategy 2: Try filtered target if different
        if (filteredTarget != null && !filteredTarget.equals(originalTarget)) {
            item = findItemInInventory(player, filteredTarget);
            if (item != null) {
                return item;
            }
        }

        // Strategy 3: Try progressive word removal
        String[] words = originalTarget.split("\\s+");
        if (words.length > 1) {
            // Try removing words from the beginning
            for (int i = 1; i < words.length; i++) {
                String partialTarget = String.join(" ", Arrays.copyOfRange(words, i, words.length));
                item = findItemInInventory(player, partialTarget);
                if (item != null) {
                    return item;
                }
            }

            // Try removing words from the end
            for (int i = words.length - 1; i > 0; i--) {
                String partialTarget = String.join(" ", Arrays.copyOfRange(words, 0, i));
                item = findItemInInventory(player, partialTarget);
                if (item != null) {
                    return item;
                }
            }
        }

        return null;
    }

    // Method to handle room item search with progressive matching
    public static Item findItemInRoom(rpg.rooms.Room room, String originalTarget, String filteredTarget) {
        // Try original target first
        Item item = room.findItem(originalTarget);
        if (item != null) {
            return item;
        }

        // Try filtered target if different
        if (filteredTarget != null && !filteredTarget.equals(originalTarget)) {
            item = room.findItem(filteredTarget);
            if (item != null) {
                return item;
            }
        }

        // Try progressive word removal for room items too
        String[] words = originalTarget.split("\\s+");
        if (words.length > 1) {
            // Try removing words from the beginning
            for (int i = 1; i < words.length; i++) {
                String partialTarget = String.join(" ", Arrays.copyOfRange(words, i, words.length));
                item = room.findItem(partialTarget);
                if (item != null) {
                    return item;
                }
            }

            // Try removing words from the end
            for (int i = words.length - 1; i > 0; i--) {
                String partialTarget = String.join(" ", Arrays.copyOfRange(words, 0, i));
                item = room.findItem(partialTarget);
                if (item != null) {
                    return item;
                }
            }
        }

        return null;
    }

    public static String buildStringFromArgs(String[] args, int start, int end) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (i > start) builder.append(" ");
            builder.append(args[i]);
        }
        return builder.toString();
    }

    // Helper method to create both original and filtered argument strings
    public static String[] createSearchTerms(String[] args) {
        String original = String.join(" ", args);
        String filtered = filterAndJoinArgs(args);
        return new String[]{original, filtered};
    }

    // Method to check if a search term contains profanity or emphasis words
    public static boolean containsEmphasisWords(String searchTerm) {
        String[] emphasisWords = {"damn", "fucking", "bloody", "goddam", "stupid", "cursed"};
        String lowerTerm = searchTerm.toLowerCase();

        for (String word : emphasisWords) {
            if (lowerTerm.contains(word)) {
                return true;
            }
        }
        return false;
    }

    // Method to remove emphasis words but keep meaningful descriptors
    public static String removeEmphasisWords(String searchTerm) {
        String[] emphasisWords = {"damn", "fucking", "bloody", "goddam", "stupid", "cursed"};
        String[] words = searchTerm.split("\\s+");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            boolean isEmphasis = false;
            for (String emphasis : emphasisWords) {
                if (word.equalsIgnoreCase(emphasis)) {
                    isEmphasis = true;
                    break;
                }
            }

            if (!isEmphasis) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(word);
            }
        }

        return result.toString();
    }
}