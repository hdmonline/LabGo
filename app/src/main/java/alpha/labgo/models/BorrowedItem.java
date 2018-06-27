package alpha.labgo.models;

public class BorrowedItem {

    public String itemImage;
    public String itemName;
    public String checkOutTime;

    public BorrowedItem(String image, String name, String time) {
        this.itemImage = image;
        this.itemName = name;
        this.checkOutTime = time;
    }
}
