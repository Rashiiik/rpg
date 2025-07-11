package rpg.items;

public abstract class Item {
    protected String name;
    protected String description;
    protected int value;
    protected boolean consumable;
    protected String[] searchKeywords; // New field for search aliases

    public Item(String name, String description, int value, boolean consumable) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.consumable = consumable;
        this.searchKeywords = new String[0]; // Default empty array
    }

    // Enhanced constructor with keywords
    public Item(String name, String description, int value, boolean consumable, String[] searchKeywords) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.consumable = consumable;
        this.searchKeywords = searchKeywords != null ? searchKeywords : new String[0];
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getValue() { return value; }
    public boolean isConsumable() { return consumable; }
    public String[] getSearchKeywords() { return searchKeywords; }

    // Method to check if this item matches a search term
    public boolean matchesSearch(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return false;
        }

        String lowerSearch = searchTerm.toLowerCase().trim();

        // Check if the search term matches the full name
        if (name.toLowerCase().contains(lowerSearch)) {
            return true;
        }

        // Check if the search term matches any of the keywords
        for (String keyword : searchKeywords) {
            if (keyword.toLowerCase().equals(lowerSearch)) {
                return true;
            }
        }

        return false;
    }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setValue(int value) { this.value = value; }
    public void setConsumable(boolean consumable) { this.consumable = consumable; }
    public void setSearchKeywords(String[] keywords) { this.searchKeywords = keywords != null ? keywords : new String[0]; }

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