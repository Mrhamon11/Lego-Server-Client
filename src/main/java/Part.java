public class Part {
    private String partNum;
    private int quantity;

    public Part(String partNum, int quantity){
        this.partNum = partNum;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getPartNum() {
        return partNum;
    }
}
