package rpg.core;

import rpg.rooms.Room;
import rpg.rooms.town.Town;
import rpg.rooms.town.Shop;
import rpg.rooms.town.Inn;
import rpg.rooms.town.TownCenter;
import rpg.rooms.outskirts.Forest;
import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    private Map<String, Room> rooms;
    private Room startingRoom;

    public RoomManager() {
        this.rooms = new HashMap<>();
        initializeRooms();
        setupConnections();
    }

    private void initializeRooms() {
        // Create all rooms
        Town town = new Town();
        Shop shop = new Shop();
        Inn inn = new Inn();
        TownCenter townCenter = new TownCenter();
        Forest forest = new Forest();

        // Add rooms to the manager
        rooms.put("town", town);
        rooms.put("shop", shop);
        rooms.put("inn", inn);
        rooms.put("towncenter", townCenter);
        rooms.put("center", townCenter);
        rooms.put("forest", forest);

        // Set starting room
        startingRoom = town;
    }

    private void setupConnections() {
        Room town = rooms.get("town");
        Room shop = rooms.get("shop");
        Room inn = rooms.get("inn");
        Room townCenter = rooms.get("towncenter");
        Room forest = rooms.get("forest");

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
}