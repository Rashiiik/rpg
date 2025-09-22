package rpg.core;

import rpg.rooms.Room;
import rpg.rooms.town.Town;
import rpg.rooms.town.Shop;
import rpg.rooms.town.Inn;
import rpg.rooms.town.TownCenter;
import rpg.rooms.tutorial.StartingAlley;
import rpg.rooms.outskirts.*;
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
        StartingAlley startingAlley = new StartingAlley();

        Town town = new Town();
        Shop shop = new Shop();
        Inn inn = new Inn();
        TownCenter townCenter = new TownCenter();
        Forest forest = new Forest();
        ForestPath forestPath = new ForestPath();
        ForestShrine forestShrine = new ForestShrine();
        ForestClearing forestClearing = new ForestClearing();
        RuinedGlade ruinedGlade = new RuinedGlade();

        rooms.put("alley", startingAlley);
        rooms.put("startingalley", startingAlley);
        rooms.put("tutorial", startingAlley);

        rooms.put("town", town);
        rooms.put("townsquare", town);
        rooms.put("square", town);
        rooms.put("shop", shop);
        rooms.put("inn", inn);
        rooms.put("towncenter", townCenter);
        rooms.put("center", townCenter);
        rooms.put("forest", forest);
        rooms.put("forestpath", forestPath);
        rooms.put("forestshrine", forestShrine);
        rooms.put("forestclearing", forestClearing);
        rooms.put("ruinedglade", ruinedGlade);

        rooms.put("basement", shop.getBasement());
        rooms.put("shopbasement", shop.getBasement());

        startingRoom = startingAlley;
    }

    private void setupConnections() {
        Room startingAlley = rooms.get("alley");
        Room town = rooms.get("town");
        Room shop = rooms.get("shop");
        Room inn = rooms.get("inn");
        Room townCenter = rooms.get("towncenter");
        Room forest = rooms.get("forest");
        Room forestPath = rooms.get("forestpath");
        Room forestShrine = rooms.get("forestshrine");
        Room forestClearing = rooms.get("forestclearing");
        Room ruinedGlade = rooms.get("ruinedglade");

        startingAlley.addConnection("forward", town);
        startingAlley.addConnection("through", town);
        startingAlley.addConnection("door", town);
        startingAlley.addConnection("town", town);
        startingAlley.addConnection("out", town);
        startingAlley.addConnection("exit", town);

        town.addConnection("north", shop);
        town.addConnection("shop", shop);
        town.addConnection("east", inn);
        town.addConnection("inn", inn);
        town.addConnection("west", townCenter);
        town.addConnection("center", townCenter);
        town.addConnection("towncenter", townCenter);
        town.addConnection("south", forest);
        town.addConnection("forest", forest);

        shop.addConnection("south", town);
        shop.addConnection("town", town);
        shop.addConnection("back", town);

        inn.addConnection("west", town);
        inn.addConnection("town", town);
        inn.addConnection("back", town);

        townCenter.addConnection("east", town);
        townCenter.addConnection("town", town);
        townCenter.addConnection("back", town);

        forest.addConnection("north", town);
        forest.addConnection("town", town);
        forest.addConnection("back", town);
        forest.addConnection("south", forestPath);
        forest.addConnection("forest path", forestPath);

        forestPath.addConnection("north", forest);
        forestPath.addConnection("forest", forest);
        forestPath.addConnection("back", forest);
        forestPath.addConnection("south", forestClearing);
        forestPath.addConnection("forest clearing", forestClearing);
        forestPath.addConnection("west", forestShrine);
        forestPath.addConnection("forest shrine", forestShrine);

        forestClearing.addConnection("north", forestPath);;
        forestClearing.addConnection("forest path", forestPath);
        forestClearing.addConnection("back", forestPath);
        forestClearing.addConnection("south", ruinedGlade);

        forestShrine.addConnection("east", forestPath);
        forestShrine.addConnection("back", forestPath);
        forestShrine.addConnection("ruined glade", ruinedGlade);

        ruinedGlade.addConnection("back", forestClearing);
        ruinedGlade.addConnection("forest shrine", forestShrine);
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

    // Method to get tutorial room specifically
    public StartingAlley getStartingAlley() {
        return (StartingAlley) rooms.get("alley");
    }
}