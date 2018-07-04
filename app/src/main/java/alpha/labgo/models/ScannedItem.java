package alpha.labgo.models;

public class ScannedItem {

    private String rfidTag;

    /**
     * Default constructor
     */
    public ScannedItem() {

    }

    /**
     * Constructor
     *
     * @param tag RFID tag
     */
    public ScannedItem(String tag) {
        this.rfidTag = tag;
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public void setRfidTag(String tag) {
        this.rfidTag = tag;
    }
}