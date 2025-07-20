package rpg.rooms;

import rpg.core.Game;
import rpg.items.Item;
import rpg.player.Player;
import rpg.utils.ItemSearchEngine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Room {
    protected String name;
    protected String description;
    protected Map<String, Room> connections;
    protected List<Item> items;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.connections = new HashMap<>();
        this.items = new ArrayList<>();
    }

    // Item management methods using ItemSearchEngine
    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public boolean removeItem(String itemName) {
        Item item = findItem(itemName);
        if (item != null) {
            return items.remove(item);
        }
        return false;
    }

    public Item findItem(String itemName) {
        return ItemSearchEngine.findItem(items, itemName);
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public boolean hasItem(String itemName) {
        return findItem(itemName) != null;
    }

    public void displayItems(Game game) {
        if (!items.isEmpty()) {
            game.getGui().displayMessage("You see:");
            for (Item item : items) {
                game.getGui().displayMessage("- " + item.getName() + " (" + item.getDescription() + ")");
            }
        }
    }

    // Abstract methods that subclasses must implement
    public abstract void enter(Game game);
    public abstract void look(Game game);

    // Connection management
    public void addConnection(String direction, Room room) {
        connections.put(direction.toLowerCase(), room);
    }

    public void removeConnection(String direction) {
        connections.remove(direction.toLowerCase());
    }

    public Room getConnectedRoom(String direction) {
        return connections.get(direction.toLowerCase());
    }

    public Map<String, Room> getConnections() {
        return new HashMap<>(connections);
    }

    public boolean hasConnection(String direction) {
        return connections.containsKey(direction.toLowerCase());
    }

    // Method that rooms can override to block movement
    public boolean attemptMove(String direction, Game game) {
        // By default, all movement is allowed
        return true;
    }

    // Method to check if movement is possible and get the destination room
    public Room tryMove(String direction, Game game) {
        // First check if the room allows this movement
        if (!attemptMove(direction, game)) {
            return null; // Movement blocked by room-specific logic
        }

        // If movement is allowed, get the connected room
        return getConnectedRoom(direction);
    }

    public void displayConnections(Game game) {
        if (connections.isEmpty()) {
            game.getGui().displayMessage("There are no exits from here.");
            return;
        }

        game.getGui().displayMessage("Available exits:");
        for (String direction : connections.keySet()) {
            Room connectedRoom = connections.get(direction);
            game.getGui().displayMessage("- " + direction + " (to " + connectedRoom.getName() + ")");
        }
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }

    public boolean handleUseItemOn(Game game, Player player, Item item, String targetName) {
        return false;
    }
}