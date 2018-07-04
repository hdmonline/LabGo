package alpha.labgo.models;

public class Item {

    private String itemImage;
    private String itemName;
    private String itemDescription;

    /**
     * Default constructor
     */
    public Item() {

    }

    /**
     * Constructor
     *
     * @param image
     * @param name
     * @param description
     */
    public Item(String image, String name, String description) {
        this.itemImage = image;
        this.itemName = name;
        this.itemDescription = description;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemImage(String image) {
        this.itemImage = image;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public void setItemDescription(String description) {
        this.itemDescription = description;
    }
}
