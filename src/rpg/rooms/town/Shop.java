package rpg.rooms.town;

import rpg.items.*;
import rpg.rooms.Room;
import rpg.core.Game;
import rpg.shop.ShopItem;
import rpg.utils.StringUtils;
import rpg.player.Player;
import java.util.ArrayList;
import java.util.List;

public class Shop extends Room {
    private List<ShopItem> shopInventory;
    private ShopBasement basement;

    public Shop() {
        super("General Store", "A well-stocked general store with shelves full of various supplies.");

        addItem(new OldCoin());
        addItem(new Key("Basement"));

        basement = new ShopBasement();

        initializeShopInventory();
        setupBasementConnection();
    }

    public boolean handleUseItemOn(Game game, Player player, Item item, String targetName) {

        if (game == null || player == null || item == null || targetName == null) {
            return false;
        }

        if (item instanceof OldCoin) {

            boolean isCounter = isCounterTarget(targetName);

            if (isCounter) {
                handleCoinOnCounter(game, player, item);
                return true;
            }
        }

        if (item instanceof Key) {
            Key key = (Key) item;

            if ("Basement".equals(key.getKeyType())) {
                boolean isBasementDoor = isBasementDoorTarget(targetName);

                if (isBasementDoor) {
                    if (game.getStoryFlags().hasFlag("basement_unlocked")) {
                        game.getGui().displayMessage("The basement door is already unlocked.");
                        return true;
                    }

                    game.getGui().displayMessage("You insert the basement key into the large keyhole.");
                    game.getGui().displayMessage("The key turns with a satisfying *click*!");

                    player.removeItem(key);

                    unlockBasement(game);
                }
            }

        }

        return false;
    }

    private boolean isBasementDoorTarget(String targetName) {
        String lower = targetName.toLowerCase();
        return lower.contains("door") ||
                lower.contains("basement") ||
                lower.contains("wooden door") ||
                lower.contains("keyhole") ||
                lower.contains("lock");
    }

    private boolean isCounterTarget(String targetName) {
        if (targetName == null) {
            return false;
        }

        String lower = targetName.toLowerCase().trim();

        boolean result = lower.equals("counter") ||
                lower.equals("wooden counter") ||
                lower.equals("indentation") ||
                lower.equals("indent") ||
                lower.equals("circular indentation") ||
                (lower.contains("counter") && lower.contains("indentation"));

        return result;
    }

    private void handleCoinOnCounter(Game game, Player player, Item coin) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case is already open.");
            return;
        }

        game.getGui().displayMessage("You place the old coin into the circular indentation on the counter.");
        game.getGui().displayMessage("The coin fits perfectly, and you hear a soft *click*.");
        game.getGui().displayMessage("The glass display case opens silently, revealing its contents.");
        game.getGui().displayMessage("A magnificent golden revolver with gleaming silver bullets is now accessible.");

        player.removeItem(coin);
        game.getStoryFlags().addFlag("opened_display_case");

        this.addItem(new GoldenRevolver());
    }

    private void setupBasementConnection() {
        basement.addConnection("up", this);
        basement.addConnection("upstairs", this);
        basement.addConnection("shop", this);
        basement.addConnection("back", this);
    }

    private void initializeShopInventory() {
        shopInventory = new ArrayList<>();

        shopInventory.add(new ShopItem(new Bullet(), 10, 8, 4));
        shopInventory.add(new ShopItem(new OldCoin(), 5, 15, 7));
    }

    @Override
    public void enter(Game game) {
        game.getGui().displayMessage("You enter the " + name);
        game.getGui().displayMessage(description);
        game.getGui().displayMessage("A friendly shopkeeper greets you with a warm smile.");
        game.getGui().displayMessage("'Welcome to my shop! Feel free to browse my wares.'");

        if (!game.getStoryFlags().hasFlag("shop_visited")) {
            game.getGui().displayMessage("You notice a peculiar display case behind the counter, gleaming despite the shop's dusty atmosphere.");
            game.getGui().displayMessage("In the corner, you spot a heavy wooden door with iron bands - it appears to lead downstairs.");
            game.getStoryFlags().addFlag("shop_visited");
        }

        game.getGui().displayMessage("Type 'buy' to see what's for sale, or 'sell' to trade your items.");
    }

    @Override
    public void look(Game game) {
        game.getGui().displayMessage("You look around the shop.");
        game.getGui().displayMessage("The shelves are lined with:");
        game.getGui().displayMessage("- Food");
        game.getGui().displayMessage("- Books and Magazines");
        game.getGui().displayMessage("- Clothes");
        game.getGui().displayMessage("- Various tools and supplies");

        game.getGui().displayMessage("");
        game.getGui().displayMessage("Behind the counter, you notice:");
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("- An open display case with velvet lining");
            game.getGui().displayMessage("- A wooden counter with a coin-shaped indentation");
        } else {
            game.getGui().displayMessage("- A locked glass display case that pulses with a quiet hum");
            game.getGui().displayMessage("- A worn wooden counter");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("In the corner, you see:");
        if (game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getGui().displayMessage("- An open wooden door leading to a basement (you can go 'down' or 'basement')");
        } else {
            game.getGui().displayMessage("- A heavy wooden door with iron bands and a large keyhole");
            game.getGui().displayMessage("- The door appears to be locked and leads downstairs");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("The shopkeeper stands behind the counter, ready to help.");
        game.getGui().displayMessage("Type 'buy' to see available items for purchase.");
        game.getGui().displayMessage("");
        displayConnections(game);

        game.getGui().displayMessage("");
        displayItems(game);
    }

    public boolean handleExamine(Game game, String target) {
        String lowerTarget = target.toLowerCase();

        if (lowerTarget.contains("counter") || lowerTarget.contains("indentation") || lowerTarget.contains("indent")) {
            examineCounter(game);
            return true;
        }

        if (lowerTarget.contains("display") || lowerTarget.contains("case") || lowerTarget.contains("glass")) {
            examineDisplayCase(game);
            return true;
        }

        if (lowerTarget.contains("shopkeeper") || lowerTarget.contains("keeper") || lowerTarget.contains("merchant")) {
            examineShopkeeper(game);
            return true;
        }

        if (lowerTarget.contains("shelf") || lowerTarget.contains("shelves")) {
            examineShelves(game);
            return true;
        }

        if (lowerTarget.contains("window") || lowerTarget.contains("windows")) {
            examineWindows(game);
            return true;
        }

        if (lowerTarget.contains("door") && (lowerTarget.contains("basement") || lowerTarget.contains("wooden") || lowerTarget.contains("iron"))) {
            examineBasementDoor(game);
            return true;
        }

        return false;
    }

    private void examineCounter(Game game) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("You run your fingers along the counter's surface.");
            game.getGui().displayMessage("The circular indentation is perfectly smooth, as if the coin melted into it.");
            game.getGui().displayMessage("You notice faint magical residue around the edges.");
        } else {
            game.getGui().displayMessage("You examine the counter closely.");
            game.getGui().displayMessage("The circular indentation is precisely carved - definitely not natural wear.");
            game.getGui().displayMessage("Running your finger around its edge, you feel a faint magical tingle.");
            game.getGui().displayMessage("This was made to hold something specific...");
        }
    }

    private void examineDisplayCase(Game game) {
        if (game.getStoryFlags().hasFlag("opened_display_case")) {
            game.getGui().displayMessage("The display case's glass is crystal clear, without a single fingerprint.");
            game.getGui().displayMessage("The opening mechanism is completely hidden - no hinges or locks visible.");
            game.getGui().displayMessage("Whatever magic opened this, it's beyond your understanding.");
        } else {
            game.getGui().displayMessage("You press your face close to the glass.");
            game.getGui().displayMessage("Inside, you can make out the shape of a magical compass.");
            game.getGui().displayMessage("The glass itself seems to shimmer with protective enchantments.");
            game.getGui().displayMessage("No amount of force would break this - it needs to be opened properly.");
        }
    }

    private void examineShopkeeper(Game game) {
        game.getGui().displayMessage("You study the shopkeeper carefully.");
        game.getGui().displayMessage("Their eyes hold ancient wisdom, far older than their appearance suggests.");
        game.getGui().displayMessage("Small scars on their hands tell of a life beyond shopkeeping.");
        game.getGui().displayMessage("When they think you're not looking, they glance meaningfully at the display case.");
    }

    private void examineShelves(Game game) {
        game.getGui().displayMessage("The shelves are packed with tools, food, and other supplies.");
    }

    private void examineWindows(Game game) {
        game.getGui().displayMessage("You peer through the grimy windows.");
        game.getGui().displayMessage("The dirt seems to obscure the view.");
    }

    private void examineBasementDoor(Game game) {
        if (game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getGui().displayMessage("The heavy wooden door stands open, revealing stone steps descending into darkness.");
            game.getGui().displayMessage("A cool breeze carries the scent of old stone and mysterious herbs from below.");
        } else {
            game.getGui().displayMessage("You examine the basement door closely.");
            game.getGui().displayMessage("The large keyhole is ornately decorated.");
            game.getGui().displayMessage("You can hear faint sounds from below - this basement is not empty.");
        }
    }

    @Override
    public boolean hasConnection(String direction) {
        String lowerDirection = direction.toLowerCase();

        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            return true;
        }

        return super.hasConnection(direction);
    }

    @Override
    public void displayConnections(Game game) {
        super.displayConnections(game);

        if (game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getGui().displayMessage("- down (to Shop Basement)");
        } else {
            game.getGui().displayMessage("- down (to basement - LOCKED, requires key)");
        }
    }

    public boolean canAccessBasement(Game game) {
        if (game != null) {
            return game.getStoryFlags().hasFlag("basement_unlocked");
        }
        return false;
    }

    public void unlockBasement(Game game) {
        if (!game.getStoryFlags().hasFlag("basement_unlocked")) {
            game.getStoryFlags().addFlag("basement_unlocked");
            game.getGui().displayMessage("You unlock the basement door with the key!");
            game.getGui().displayMessage("You can now go 'down' to explore the basement.");
        }
    }

    public ShopBasement getBasement() {
        return basement;
    }

    @Override
    public boolean attemptMove(String direction, Game game) {
        String lowerDirection = direction.toLowerCase();

        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            if (!canAccessBasement(game)) {
                game.getGui().displayMessage("The basement door is locked. You need to find a way to unlock it first.");
                game.getGui().displayMessage("The heavy wooden door has a large keyhole - maybe you need a key?");
                return false;
            }
        }

        return super.attemptMove(direction, game);
    }

    @Override
    public Room getConnectedRoom(String direction) {
        String lowerDirection = direction.toLowerCase();

        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            return basement;
        }

        return super.getConnectedRoom(direction);
    }

    @Override
    public Room tryMove(String direction, Game game) {
        String lowerDirection = direction.toLowerCase();

        if (lowerDirection.equals("down") || lowerDirection.equals("basement") || lowerDirection.equals("downstairs")) {
            if (!canAccessBasement(game)) {
                game.getGui().displayMessage("The basement door is locked. You need to find a way to unlock it first.");
                game.getGui().displayMessage("The heavy wooden door has a large keyhole - maybe you need a key?");
                return null;
            }
            return basement;
        }

        return super.tryMove(direction, game);
    }

    public void displayShopInventory(Game game) {
        game.getGui().displayMessage("=== SHOP INVENTORY ===");

        if (shopInventory.isEmpty()) {
            game.getGui().displayMessage("The shop is currently out of stock.");
            return;
        }

        boolean hasItems = false;
        for (ShopItem shopItem : shopInventory) {
            if (shopItem.canBuy() && shopItem.hasStock()) {
                hasItems = true;
                String stockText = shopItem.getStock() > 1 ?
                        " (Stock: " + shopItem.getStock() + ")" : "";
                game.getGui().displayMessage("- " + shopItem.getItem().getName() +
                        " - " + shopItem.getBuyPrice() + " gold" + stockText);
                game.getGui().displayMessage("  " + shopItem.getItem().getDescription());
            }
        }

        if (!hasItems) {
            game.getGui().displayMessage("No items currently available for purchase.");
        }

        game.getGui().displayMessage("");
        game.getGui().displayMessage("Usage: buy <item name> [quantity]");
        game.getGui().displayMessage("       sell <item name> [quantity]");
    }

    public ShopItem findShopItem(String itemName) {
        if (StringUtils.isNullOrEmpty(itemName)) {
            return null;
        }

        String searchTerm = StringUtils.safeTrim(itemName).toLowerCase();

        for (ShopItem shopItem : shopInventory) {
            if (shopItem.getItem().getName().toLowerCase().contains(searchTerm)) {
                return shopItem;
            }

            if (shopItem.getItem().matchesSearch(searchTerm)) {
                return shopItem;
            }
        }

        return null;
    }

    public List<ShopItem> getShopInventory() {
        return new ArrayList<>(shopInventory);
    }

    public void addShopItem(ShopItem shopItem) {
        if (shopItem != null) {
            shopInventory.add(shopItem);
        }
    }

    public void removeShopItem(ShopItem shopItem) {
        shopInventory.remove(shopItem);
    }

    public void restockShopItem(String itemName, int quantity) {
        ShopItem shopItem = findShopItem(itemName);
        if (shopItem != null) {
            shopItem.addStock(quantity);
        }
    }

    public void addToShopInventory(Item item, int quantity) {
        ShopItem existingShopItem = findShopItem(item.getName());
        if (existingShopItem != null && existingShopItem.canSell()) {
            existingShopItem.addStock(quantity);
        } else {
            ShopItem newShopItem = new ShopItem(item, quantity);
            shopInventory.add(newShopItem);
        }
    }
}