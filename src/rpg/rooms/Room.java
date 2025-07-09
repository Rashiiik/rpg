package rpg.rooms;

import rpg.core.Game;
import java.util.HashMap;
import java.util.Map;

public abstract class Room {
    protected String name;
    protected String description;
    protected Map<String, Room> connections;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
        this.connections = new HashMap<>();
    }

    public abstract void enter(Game game);
    public abstract void look(Game game);

    // Connection management methods
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
        return new HashMap<>(connections); // Return a copy to prevent external modification
    }

    public boolean hasConnection(String direction) {
        return connections.containsKey(direction.toLowerCase());
    }

    // Display available connections
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
}