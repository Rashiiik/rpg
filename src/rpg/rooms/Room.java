package rpg.rooms;

import rpg.core.Game;

public abstract class Room {
    protected String name;
    protected String description;

    public Room(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public abstract void enter(Game game);
    public abstract void look(Game game);

    public String getName() { return name; }
    public String getDescription() { return description; }
}