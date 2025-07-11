package rpg.items;

import rpg.player.Player;

public class Bullet extends Item {
    private int damage;

    public Bullet() {
        super("Bullet", "A standard bullet for firearms", 5, true,
                new String[]{"bullet", "ammo", "ammunition", "round"});
        this.damage = 10;
    }

    public Bullet(String name, String description, int value, int damage) {
        super(name, description, value, true,
                new String[]{"bullet", "ammo", "ammunition", "round"});
        this.damage = damage;
    }

    @Override
    public void use(Player player) {
        // This would be used in combat or with a weapon
        player.getGame().getGui().displayMessage("You load the bullet into your weapon.");
    }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
}