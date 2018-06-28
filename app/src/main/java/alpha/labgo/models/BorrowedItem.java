package alpha.labgo.models;

public class BorrowedItem {

    public String itemImage;
    public String itemName;
    public String itemDescription;
    public String checkOutTime;

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
}
