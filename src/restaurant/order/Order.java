package restaurant.order;

public class Order {

    public enum orderType {
        SOUP,
        MAIN_COURSE,
        DESSERT,
        DRIMG
    }

    private int orderNumber;
    private orderType orderType;
    private double endServiceTime;

    public Order(int orderNumber, Order.orderType orderType, double endServiceTime) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.endServiceTime = endServiceTime;
    }

    public Order(int orderNumber, double endServiceTime) {
        this.orderNumber = orderNumber;
        this.endServiceTime = endServiceTime;
    }

    public Order(int orderNumber, Order.orderType orderType) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Order.orderType getOrderType() {
        return orderType;
    }

    public void setOrderType(Order.orderType orderType) {
        this.orderType = orderType;
    }

    public double getEndServiceTime() {
        return endServiceTime;
    }

    public void setEndServiceTime(double endServiceTime) {
        this.endServiceTime = endServiceTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber=" + orderNumber +
                ", orderType=" + orderType +
                ", endServiceTime=" + endServiceTime +
                '}';
    }
}