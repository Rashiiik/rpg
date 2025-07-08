package rpg.player;

public class Player {
    private String name;
    private int level;
    private int hp;
    private int maxHp;
    private int attack;
    private int defense;
    private int xp;
    private int gold;

    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.maxHp = 100;
        this.hp = maxHp;
        this.attack = 10;
        this.defense = 5;
        this.xp = 0;
        this.gold = 50;
    }

    // Getters
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getXp() { return xp; }
    public int getGold() { return gold; }

    // Setters
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public void setGold(int gold) { this.gold = Math.max(0, gold); }

    // Game methods
    public void takeDamage(int damage) {
        int actualDamage = Math.max(1, damage - defense);
        hp = Math.max(0, hp - actualDamage);
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void gainGold(int amount) {
        gold += amount;
    }

    public boolean canAfford(int cost) {
        return gold >= cost;
    }

    public void spendGold(int amount) {
        if (canAfford(amount)) {
            gold -= amount;
        }
    }
}