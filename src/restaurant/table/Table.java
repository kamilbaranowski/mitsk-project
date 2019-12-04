package restaurant.table;

public class Table {
    private int tableNumber;
    private boolean isFree;

    public Table(int tableNumber){
        this.tableNumber = tableNumber;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public void changeIsFree() {
        if(isFree == true) isFree = false;
        else isFree = true;
    }

}
