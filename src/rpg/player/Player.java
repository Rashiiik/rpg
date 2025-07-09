package rpg.player;

public class Player {
    private String name;
    private int level;
    private int hp;
    private int maxHp;
    private int gold;
    private int experience;
    private int experienceToNextLevel;
    private int strength;
    private int defense;

    public Player(String name) {
        this.name = name;
        this.level = 1;
        this.maxHp = 100;
        this.hp = maxHp;
        this.gold = 50;
        this.experience = 0;
        this.experienceToNextLevel = 100;
        this.strength = 10;
        this.defense = 5;
    }

    // Getters
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getHp() { return hp; }
    public int getMaxHp() { return maxHp; }
    public int getGold() { return gold; }
    public int getExperience() { return experience; }
    public int getExperienceToNextLevel() { return experienceToNextLevel; }
    public int getStrength() { return strength; }
    public int getDefense() { return defense; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setLevel(int level) { this.level = level; }
    public void setHp(int hp) { this.hp = Math.max(0, Math.min(hp, maxHp)); }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public void setGold(int gold) { this.gold = Math.max(0, gold); }
    public void setExperience(int experience) { this.experience = experience; }
    public void setExperienceToNextLevel(int experienceToNextLevel) { this.experienceToNextLevel = experienceToNextLevel; }
    public void setStrength(int strength) { this.strength = strength; }
    public void setDefense(int defense) { this.defense = defense; }

    // Utility methods
    public void heal(int amount) {
        setHp(hp + amount);
    }

    public void takeDamage(int damage) {
        setHp(hp - damage);
    }

    public void addGold(int amount) {
        setGold(gold + amount);
    }

    public boolean spendGold(int amount) {
        if (gold >= amount) {
            setGold(gold - amount);
            return true;
        }
        return false;
    }

    public void addExperience(int amount) {
        experience += amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (experience >= experienceToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        experience -= experienceToNextLevel;
        experienceToNextLevel = level * 100; // Simple formula

        // Increase stats on level up
        maxHp += 20;
        hp = maxHp; // Full heal on level up
        strength += 2;
        defense += 1;
    }
}