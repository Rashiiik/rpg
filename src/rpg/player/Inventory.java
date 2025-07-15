package rpg.player;

import rpg.items.Item;
import rpg.utils.ItemSearchEngine;
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

    // Delegate to centralized search engine
    public Item findItem(String itemName) {
        return ItemSearchEngine.findItem(items, itemName);
    }

    // Enhanced findItem method that tries multiple search strategies
    public Item findItemProgressive(String originalTerm, String filteredTerm) {
        return ItemSearchEngine.findItemProgressive(items, originalTerm, filteredTerm);
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