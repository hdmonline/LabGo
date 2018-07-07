package alpha.labgo.models;

public class InventoryItem {

    private String itemImage;
    private String itemName;
    private String itemDescription;
    private int itemQuantity;

    /**
     * Default constructor
     */
    public InventoryItem() {

    }

    /**
     * Constructor
     *
     * @param image     URL for the item image
     * @param name      Item name
     * @param description   Item description
     * @param quantity      Item quantity
     */
    public InventoryItem(String image, String name, String description, int quantity) {
        this.itemImage = image;
        this.itemName = name;
        this.itemDescription = description;
        this.itemQuantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemImage() {
        return itemImage;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
