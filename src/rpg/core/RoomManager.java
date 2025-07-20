package rpg.core;

import rpg.rooms.Room;
import rpg.rooms.town.Town;
import rpg.rooms.town.Shop;
import rpg.rooms.town.Inn;
import rpg.rooms.town.TownCenter;
import rpg.rooms.town.ShopBasement;
import rpg.rooms.tutorial.StartingAlley;
import rpg.rooms.outskirts.Forest;
import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private Map<String, Room> rooms;
    private Room startingRoom;
    // REMOVED: currentRoom field - Game class handles current room tracking

    public RoomManager() {
        this.rooms = new HashMap<>();
        initializeRooms();
        setupConnections();
        // REMOVED: currentRoom initialization
    }

    private void initializeRooms() {
        // Create tutorial room
        StartingAlley startingAlley = new StartingAlley();

        // Create main game rooms
        Town town = new Town();
        Shop shop = new Shop();
        Inn inn = new Inn();
        TownCenter townCenter = new TownCenter();
        Forest forest = new Forest();

        // Add tutorial room to the manager
        rooms.put("alley", startingAlley);
        rooms.put("startingalley", startingAlley);
        rooms.put("tutorial", startingAlley);

        // Add main game rooms to the manager
        rooms.put("town", town);
        rooms.put("townsquare", town);
        rooms.put("square", town);
        rooms.put("shop", shop);
        rooms.put("inn", inn);
        rooms.put("towncenter", townCenter);
        rooms.put("center", townCenter);
        rooms.put("forest", forest);

        // Add the basement (accessed through shop)
        rooms.put("basement", shop.getBasement());
        rooms.put("shopbasement", shop.getBasement());

        // Set starting room to the tutorial alley
        startingRoom = startingAlley;
    }

    private void setupConnections() {
        // Get rooms
        Room startingAlley = rooms.get("alley");
        Room town = rooms.get("town");
        Room shop = rooms.get("shop");
        Room inn = rooms.get("inn");
        Room townCenter = rooms.get("towncenter");
        Room forest = rooms.get("forest");

        // Connect alley directly to town (this connection will be managed by the alley's movement logic)
        startingAlley.addConnection("forward", town);
        startingAlley.addConnection("through", town);
        startingAlley.addConnection("door", town);
        startingAlley.addConnection("town", town);
        startingAlley.addConnection("out", town);
        startingAlley.addConnection("exit", town);

        // Town connections
        town.addConnection("north", shop);
        town.addConnection("shop", shop);
        town.addConnection("east", inn);
        town.addConnection("inn", inn);
        town.addConnection("west", townCenter);
        town.addConnection("center", townCenter);
        town.addConnection("towncenter", townCenter);
        town.addConnection("south", forest);
        town.addConnection("forest", forest);

        // Shop connections (back to town)
        shop.addConnection("south", town);
        shop.addConnection("town", town);
        shop.addConnection("back", town);
        // Note: Basement connections are handled within the Shop class

        // Inn connections (back to town)
        inn.addConnection("west", town);
        inn.addConnection("town", town);
        inn.addConnection("back", town);

        // Town Center connections (back to town)
        townCenter.addConnection("east", town);
        townCenter.addConnection("town", town);
        townCenter.addConnection("back", town);

        // Forest connections (back to town)
        forest.addConnection("north", town);
        forest.addConnection("town", town);
        forest.addConnection("back", town);
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId.toLowerCase());
    }

    public Room getStartingRoom() {
        return startingRoom;
    }

    // REMOVED: getCurrentRoom() and setCurrentRoom() methods
    // Current room is now managed exclusively by the Game class

    public Map<String, Room> getAllRooms() {
        return new HashMap<>(rooms);
    }

    public void addRoom(String roomId, Room room) {
        rooms.put(roomId.toLowerCase(), room);
    }

    public void removeRoom(String roomId) {
        rooms.remove(roomId.toLowerCase());
    }

    public boolean hasRoom(String roomId) {
        return rooms.containsKey(roomId.toLowerCase());
    }

    // Method to get tutorial room specifically
    public StartingAlley getStartingAlley() {
        return (StartingAlley) rooms.get("alley");
    }
}