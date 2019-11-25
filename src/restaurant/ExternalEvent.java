package restaurant;


import java.util.Comparator;

public class ExternalEvent {

    public enum EventType {RESERVE, START, END}
    public enum EventServiceStand {Cash, Queue, Table}

    private int customerNumber;
    private String dishType;
    private EventType eventType;
    private EventServiceStand eventServiceStandtannd;
    private Double time;

    public ExternalEvent(Double time, EventType eventType, EventServiceStand eventServiceStand, int customerNumber) {
        this.customerNumber = customerNumber;
        this.eventType = eventType;
        this.time = time;
        this.eventServiceStandtannd= eventServiceStand;
    }

    public ExternalEvent(Double time, EventType eventType, EventServiceStand eventServiceStand, int carNumber, String dishType) {
        this.customerNumber = carNumber;
        this.eventType = eventType;
        this.time = time;
        this.eventServiceStandtannd= eventServiceStand;
        this.dishType= dishType;
    }


    public EventType getEventType() {
        return eventType;
    }

    public EventServiceStand getEventServiceStandtannd() {
        return eventServiceStandtannd;
    }

    public double getTime() {
        return time;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public String getDishType() {
        return dishType;
    }

    public static class ExternalEventComparator implements Comparator<ExternalEvent> {

        @Override
        public int compare(ExternalEvent o1, ExternalEvent o2) {
            return o1.time.compareTo(o2.time);
        }
    }

}
