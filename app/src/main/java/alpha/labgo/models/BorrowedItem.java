package alpha.labgo.models;

public class BorrowedItem {

    private String itemImage;
    private String itemName;
    private String itemDescription;
    private String checkOutTime;

    /**
     * Default constructor
     */
    public BorrowedItem() {

    }

    /**
     * Constructor
     *
     * @param image
     * @param name
     * @param description
     * @param time
     */
    public BorrowedItem(String image, String name, String description, String time) {
        this.itemImage = image;
        this.itemName = name;
        this.itemDescription = description;
        this.checkOutTime = time;
    }

    public String getItemImage() {
        return itemImage;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
