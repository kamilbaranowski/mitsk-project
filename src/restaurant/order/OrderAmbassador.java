package restaurant.order;

import restaurant.Ambassador;
import restaurant.Federate;

public class OrderAmbassador extends Ambassador {

    private OrderFederate orderFederate;

    public OrderAmbassador(OrderFederate orderFederate) {
        this.orderFederate = orderFederate;
    }
}
