package rpg.rooms;

import rpg.core.Game;
import rpg.items.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Room {
    protected String name;
    protected String description;
    protected Map<String, Room> connections;
    protected List<Item> items; // ADD THIS

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.connections = new HashMap<>();
        this.items = new ArrayList<>(); // ADD THIS
    }

    // ADD ALL THESE ITEM MANAGEMENT METHODS
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
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    public List<Item> getItems() {
        return new ArrayList<>(items);
    }

    public boolean hasItem(String itemName) {
        return findItem(itemName) != null;
    }

    // ADD THIS METHOD
    public void displayItems(Game game) {
        if (!items.isEmpty()) {
            game.getGui().displayMessage("You see:");
            for (Item item : items) {
                game.getGui().displayMessage("- " + item.getName() + " (" + item.getDescription() + ")");
            }
        }
    }

    // EXISTING METHODS REMAIN THE SAME
    public abstract void enter(Game game);
    public abstract void look(Game game);

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

    public String getName() { return name; }
    public String getDescription() { return description; }
}