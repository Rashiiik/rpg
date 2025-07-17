package rpg.utils;

import rpg.items.Item;
import rpg.player.Player;
import rpg.rooms.Room;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class ItemSearchEngine {

    public static <T extends Item> T findItem(List<T> items, String searchTerm) {
        if (StringUtils.isNullOrEmpty(searchTerm) || items == null) {
            return null;
        }

        String cleanTerm = StringUtils.safeTrim(searchTerm).toLowerCase();

        // Strategy 1: Exact name match
        T result = findByExactName(items, cleanTerm);
        if (result != null) return result;

        // Strategy 2: Partial name matching
        result = findByPartialName(items, cleanTerm);
        if (result != null) return result;

        // Strategy 3: Word-by-word matching
        result = findByWordMatching(items, cleanTerm);
        if (result != null) return result;

        // Strategy 4: Item's own matching method
        result = findByItemMatcher(items, cleanTerm);
        if (result != null) return result;

        // Strategy 5: Try with filtered search term
        String filteredTerm = filterSearchTerm(cleanTerm);
        if (!filteredTerm.equals(cleanTerm)) {
            return findItem(items, filteredTerm);
        }

        return null;
    }

    public static <T extends Item> T findItemProgressive(List<T> items, String originalTerm, String filteredTerm) {
        // Try original term first
        T result = findItem(items, originalTerm);
        if (result != null) return result;

        // Try filtered term if different
        if (!StringUtils.isNullOrEmpty(filteredTerm) && !filteredTerm.equals(originalTerm)) {
            result = findItem(items, filteredTerm);
            if (result != null) return result;
        }

        // Try progressive word removal
        return findWithWordRemoval(items, originalTerm);
    }

    public static Item findInInventory(Player player, String searchTerm) {
        return findItem(player.getInventory().getItems(), searchTerm);
    }

    public static Item findInInventoryProgressive(Player player, String originalTerm, String filteredTerm) {
        return findItemProgressive(player.getInventory().getItems(), originalTerm, filteredTerm);
    }

    public static Item findInRoom(Room room, String searchTerm) {
        return findItem(room.getItems(), searchTerm);
    }

    public static Item findInRoomProgressive(Room room, String originalTerm, String filteredTerm) {
        return findItemProgressive(room.getItems(), originalTerm, filteredTerm);
    }

    // Private helper methods
    private static <T extends Item> T findByExactName(List<T> items, String searchTerm) {
        for (T item : items) {
            if (item.getName().toLowerCase().equals(searchTerm)) {
                return item;
            }
        }
        return null;
    }

    private static <T extends Item> T findByPartialName(List<T> items, String searchTerm) {
        for (T item : items) {
            if (item.getName().toLowerCase().contains(searchTerm)) {
                return item;
            }
        }
        return null;
    }

    private static <T extends Item> T findByWordMatching(List<T> items, String searchTerm) {
        String[] searchWords = searchTerm.split("\\s+");

        // Try matching all words
        for (T item : items) {
            if (matchesAllWords(item.getName().toLowerCase(), searchWords)) {
                return item;
            }
        }

        // Fallback: try single word matching
        for (String searchWord : searchWords) {
            if (searchWord.length() > 2) { // Skip very short words
                for (T item : items) {
                    String[] itemWords = item.getName().toLowerCase().split("\\s+");
                    for (String itemWord : itemWords) {
                        if (itemWord.equals(searchWord)) {
                            return item;
                        }
                    }
                }
            }
        }

        return null;
    }

    private static <T extends Item> T findByItemMatcher(List<T> items, String searchTerm) {
        for (T item : items) {
            if (item.matchesSearch(searchTerm)) {
                return item;
            }
        }
        return null;
    }

    private static <T extends Item> T findWithWordRemoval(List<T> items, String originalTerm) {
        String[] words = originalTerm.split("\\s+");
        if (words.length <= 1) {
            return null;
        }

        // Try removing words from the beginning
        for (int i = 1; i < words.length; i++) {
            String partialTerm = String.join(" ", Arrays.copyOfRange(words, i, words.length));
            T result = findItem(items, partialTerm);
            if (result != null) return result;
        }

        // Try removing words from the end
        for (int i = words.length - 1; i > 0; i--) {
            String partialTerm = String.join(" ", Arrays.copyOfRange(words, 0, i));
            T result = findItem(items, partialTerm);
            if (result != null) return result;
        }

        return null;
    }

    private static boolean matchesAllWords(String itemName, String[] searchWords) {
        String[] itemWords = itemName.split("\\s+");

        for (String searchWord : searchWords) {
            boolean wordFound = false;
            for (String itemWord : itemWords) {
                if (itemWord.equals(searchWord)) {
                    wordFound = true;
                    break;
                }
            }
            if (!wordFound) {
                return false;
            }
        }
        return true;
    }

    /**
     * Filter out filler words from search term
     */
    public static String filterSearchTerm(String searchTerm) {
        if (StringUtils.isNullOrEmpty(searchTerm)) {
            return "";
        }

        String[] words = searchTerm.split("\\s+");
        StringBuilder filtered = new StringBuilder();

        return filtered.toString();
    }

    public static String[] createSearchTerms(String[] args) {
        String original = String.join(" ", args);
        String filtered = filterSearchTerm(original);
        return new String[]{original, filtered};
    }
}