package restaurant.order;

import hla.rti1516e.InteractionClassHandle;
import restaurant.Ambassador;
import restaurant.Federate;

public class OrderAmbassador extends Ambassador {

    private OrderFederate orderFederate;

    protected InteractionClassHandle orderExecutionHandle;
    protected InteractionClassHandle placeOrderHandle;



    public OrderAmbassador(OrderFederate orderFederate) {
        this.orderFederate = orderFederate;
    }
}
