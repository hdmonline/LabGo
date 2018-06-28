package alpha.labgo.models;

public class InventoryItem {

    public String itemImage;
    public String itemName;
    public String itemDescription;
    public int itemQuantity;

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
}
