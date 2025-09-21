package rpg.shop;

import rpg.items.Item;

public class ShopItem {
    private Item item;
    private int stock;
    private int buyPrice;
    private int sellPrice;
    private boolean canBuy;
    private boolean canSell;

    public ShopItem(Item item, int stock, int buyPrice, int sellPrice) {
        this.item = item;
        this.stock = stock;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.canBuy = true;
        this.canSell = true;
    }

    public ShopItem(Item item, int stock) {
        this.item = item;
        this.stock = stock;
        this.buyPrice = item.getValue();
        this.sellPrice = (int) (item.getValue() * 0.5);
        this.canBuy = true;
        this.canSell = true;
    }

    public boolean hasStock() {
        return stock > 0;
    }

    public boolean reduceStock(int amount) {
        if (stock >= amount) {
            stock -= amount;
            return true;
        }
        return false;
    }

    public void addStock(int amount) {
        stock += amount;
    }

    public Item getItem() { return item; }
    public int getStock() { return stock; }
    public int getBuyPrice() { return buyPrice; }
    public int getSellPrice() { return sellPrice; }
    public boolean canBuy() { return canBuy; }
    public boolean canSell() { return canSell; }
    public void setStock(int stock) { this.stock = stock; }
    public void setBuyPrice(int buyPrice) { this.buyPrice = buyPrice; }
    public void setSellPrice(int sellPrice) { this.sellPrice = sellPrice; }
    public void setCanBuy(boolean canBuy) { this.canBuy = canBuy; }
    public void setCanSell(boolean canSell) { this.canSell = canSell; }
}