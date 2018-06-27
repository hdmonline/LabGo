package alpha.labgo.models;

public class BorrowedItem {

    public String itemImage;
    public String itemName;
    public String checkOutTime;

    /**
     * Default constructor
     */
    public BorrowedItem() {

    }

    /**
     * Constructor
     * @param image Image URL
     * @param name  Tool name
     * @param time  Tool check out time
     */
    public BorrowedItem(String image, String name, String time) {
        this.itemImage = image;
        this.itemName = name;
        this.checkOutTime = time;
    }
}
