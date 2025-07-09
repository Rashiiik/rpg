package rpg.items;

public abstract class Item {
    protected String name;
    protected String description;
    protected int value;
    protected boolean consumable;

    public Item(String name, String description, int value, boolean consumable) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.consumable = consumable;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getValue() { return value; }
    public boolean isConsumable() { return consumable; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setValue(int value) { this.value = value; }
    public void setConsumable(boolean consumable) { this.consumable = consumable; }

    // Abstract method for using the item
    public abstract void use(rpg.player.Player player);

    // Method to get display information
    public String getDisplayInfo() {
        return name + " - " + description + " (Value: " + value + " gold)";
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Item item = (Item) obj;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}