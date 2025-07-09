package rpg.items;

import rpg.player.Player;

public class Sword extends Item {

    public Sword() {
        super("Sword", "A sharp steel sword for combat", 100, false);
    }

    @Override
    public void use(Player player) {
        // Swords aren't typically "used" but could be equipped
        // For now, just display a message
        System.out.println("You brandish the sword. It gleams in the light.");
    }
}