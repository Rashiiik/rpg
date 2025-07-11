package rpg.player;

import rpg.items.Item;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<Item> items;
    private int maxSize;

    public Inventory(int maxSize) {
        this.items = new ArrayList<>();
        this.maxSize = maxSize;
    }

    public Inventory() {
        this(50); // Default max size
    }

    // Add item to inventory
    public boolean addItem(Item item) {
        if (items.size() >= maxSize) {
            return false; // Inventory full
        }
        items.add(item);
        return true;
    }

    // Remove item from inventory
    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    // Remove item by name
    public boolean removeItem(String itemName) {
        Item item = findItem(itemName);
        if (item != null) {
            return items.remove(item);
        }
        return false;
    }

    // Remove specific quantity of items by name
    public boolean removeItems(String itemName, int quantity) {
        if (quantity <= 0) return false;

        int removed = 0;
        List<Item> itemsToRemove = new ArrayList<>();

        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                itemsToRemove.add(item);
                removed++;
                if (removed >= quantity) {
                    break;
                }
            }
        }

        if (removed >= quantity) {
            items.removeAll(itemsToRemove);
            return true;
        }

        return false;
    }

    // Get count of specific item by name
    public int getItemCount(String itemName) {
        int count = 0;
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                count++;
            }
        }
        return count;
    }

    public Item findItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }

        String searchTerm = itemName.toLowerCase().trim();

        // Strategy 1: Try exact name match first
        Item result = findByExactName(searchTerm);
        if (result != null) return result;

        // Strategy 2: Try partial name matching (contains)
        result = findByPartialName(searchTerm);
        if (result != null) return result;

        // Strategy 3: Try word-by-word matching
        result = findByWordMatching(searchTerm);
        if (result != null) return result;

        // Strategy 4: Try keyword/alias matching
        result = findByKeywords(searchTerm);
        if (result != null) return result;

        // Strategy 5: Try filtered search (remove common articles)
        String filteredSearch = filterSearchTerm(searchTerm);
        if (!filteredSearch.equals(searchTerm)) {
            return findItem(filteredSearch); // Recursive call with filtered term
        }

        return null;
    }

    private Item findByExactName(String searchTerm) {
        for (Item item : items) {
            if (item.getName().toLowerCase().equals(searchTerm)) {
                return item;
            }
        }
        return null;
    }

    private Item findByPartialName(String searchTerm) {
        for (Item item : items) {
            if (item.getName().toLowerCase().contains(searchTerm)) {
                return item;
            }
        }
        return null;
    }

    private Item findByWordMatching(String searchTerm) {
        String[] searchWords = searchTerm.split("\\s+");

        for (Item item : items) {
            String itemNameLower = item.getName().toLowerCase();
            String[] itemWords = itemNameLower.split("\\s+");

            // Check if all search words are found in item name
            boolean allWordsFound = true;
            for (String searchWord : searchWords) {
                boolean wordFound = false;
                for (String itemWord : itemWords) {
                    if (itemWord.equals(searchWord)) {
                        wordFound = true;
                        break;
                    }
                }
                if (!wordFound) {
                    allWordsFound = false;
                    break;
                }
            }

            if (allWordsFound) {
                return item;
            }
        }

        // Fallback: try single word matching
        for (String searchWord : searchWords) {
            for (Item item : items) {
                String itemNameLower = item.getName().toLowerCase();
                String[] itemWords = itemNameLower.split("\\s+");

                for (String itemWord : itemWords) {
                    if (itemWord.equals(searchWord)) {
                        return item;
                    }
                }
            }
        }

        return null;
    }

    private Item findByKeywords(String searchTerm) {
        for (Item item : items) {
            if (item.matchesSearch(searchTerm)) {
                return item;
            }
        }
        return null;
    }

    private String filterSearchTerm(String searchTerm) {
        String[] words = searchTerm.split("\\s+");
        StringBuilder filtered = new StringBuilder();

        for (String word : words) {
            // Remove common articles and filler words
            if (!isFillerWord(word)) {
                if (filtered.length() > 0) {
                    filtered.append(" ");
                }
                filtered.append(word);
            }
        }

        return filtered.toString();
    }

    private boolean isFillerWord(String word) {
        return word.equals("the") || word.equals("a") || word.equals("an") ||
                word.equals("damn") || word.equals("fucking") || word.equals("bloody");
    }

    // Enhanced findItem method that tries multiple search strategies
    public Item findItemProgressive(String originalTerm, String filteredTerm) {
        if (originalTerm == null || originalTerm.trim().isEmpty()) {
            return null;
        }

        // Try original term first
        Item result = findItem(originalTerm);
        if (result != null) return result;

        // Try filtered term if different
        if (filteredTerm != null && !filteredTerm.equals(originalTerm)) {
            result = findItem(filteredTerm);
            if (result != null) return result;
        }

        return null;
    }

    // Check if inventory contains item
    public boolean hasItem(String itemName) {
        return findItem(itemName) != null;
    }

    // Check if inventory contains specific item object
    public boolean hasItem(Item item) {
        return items.contains(item);
    }

    // Get all items
    public List<Item> getItems() {
        return new ArrayList<>(items); // Return copy to prevent external modification
    }

    // Get items by type
    public List<Item> getItemsByType(Class<? extends Item> type) {
        List<Item> result = new ArrayList<>();
        for (Item item : items) {
            if (type.isInstance(item)) {
                result.add(item);
            }
        }
        return result;
    }

    // Get inventory size
    public int getSize() {
        return items.size();
    }

    // Get max inventory size
    public int getMaxSize() {
        return maxSize;
    }

    // Check if inventory is full
    public boolean isFull() {
        return items.size() >= maxSize;
    }

    // Check if inventory is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Clear inventory
    public void clear() {
        items.clear();
    }

    // Get formatted inventory display
    public String getInventoryDisplay() {
        if (isEmpty()) {
            return "Your inventory is empty.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Inventory (" + getSize() + "/" + getMaxSize() + ") ===\n");

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            sb.append((i + 1) + ". " + item.getDisplayInfo() + "\n");
        }

        return sb.toString();
    }

    // Get total value of all items
    public int getTotalValue() {
        int total = 0;
        for (Item item : items) {
            total += item.getValue();
        }
        return total;
    }

    // Set max size
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}