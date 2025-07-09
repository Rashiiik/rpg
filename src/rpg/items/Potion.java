package rpg.items;

import rpg.player.Player;

public class Potion extends Item {
    private int healAmount;

    public Potion() {
        super("Health Potion", "Restores 50 HP when consumed", 25, true);
        this.healAmount = 50;
    }

    public Potion(String name, String description, int value, int healAmount) {
        super(name, description, value, true);
        this.healAmount = healAmount;
    }

    @Override
    public void use(Player player) {
        int oldHp = player.getHp();
        player.heal(healAmount);
        int healedAmount = player.getHp() - oldHp;

        System.out.println("You drink the " + getName() + " and restore " + healedAmount + " HP.");

        // Remove the potion from inventory after use (since it's consumable)
        player.removeItem(this);
    }

    public int getHealAmount() {
        return healAmount;
    }
}