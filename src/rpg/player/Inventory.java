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

    public Item findItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }

        String searchTerm = itemName.toLowerCase().trim();

        // First pass: try exact name match
        for (Item item : items) {
            if (item.getName().toLowerCase().equals(searchTerm)) {
                return item;
            }
        }

        // Second pass: try partial matching - check if any word in the item name matches
        for (Item item : items) {
            String itemNameLower = item.getName().toLowerCase();

            // Check if search term matches any word in the item name
            String[] words = itemNameLower.split("\\s+");
            for (String word : words) {
                if (word.equals(searchTerm)) {
                    return item;
                }
            }
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