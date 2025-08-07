package rpg.player;

import rpg.core.Game;

public class Player {
    private String name;
    private int hp;
    private int maxHp;
    private int gold;
    private Inventory inventory;
    private Game game;

    public Player(String name) {
        this.name = name;
        this.maxHp = 100;
        this.hp = maxHp;
        this.gold = 50;
        this.inventory = new Inventory(50); // 50 item slots
    }

    // Getters
    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getGold() { return gold; }
    public Inventory getInventory() { return inventory; }

    public Game getGame() {
        return game;
    }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public void setGold(int gold) { this.gold = Math.max(0, gold); }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    public void setGame(Game game) {
        this.game = game;
    }

    // Utility methods
    public void heal(int amount) {
        setHp(hp + amount);
    }

    public void addGold(int amount) {
        setGold(gold + amount);
    }

    public boolean removeGold(int amount) {
        if (gold >= amount) {
            setGold(gold - amount);
            return true;
        }
        return false;
    }

    // Inventory convenience methods
    public boolean addItem(rpg.items.Item item) {
        return inventory.addItem(item);
    }

    public boolean removeItem(rpg.items.Item item) {
        return inventory.removeItem(item);
    }

    public boolean removeItem(String itemName) {
        return inventory.removeItem(itemName);
    }

    public boolean hasItem(String itemName) {
        return inventory.hasItem(itemName);
    }

    public rpg.items.Item findItem(String itemName) {
        return inventory.findItem(itemName);
    }
}