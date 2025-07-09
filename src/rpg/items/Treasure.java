package rpg.items;

import rpg.player.Player;

public class Treasure extends Item {

    public Treasure(String name, String description, int value) {
        super(name, description, value, false);
    }

    public Treasure() {
        super("Gold Coin", "A shiny gold coin", 10, false);
    }

    @Override
    public void use(Player player) {
        // Treasures are typically sold, not used
        System.out.println("This " + getName() + " is valuable. You should consider selling it.");
    }
}