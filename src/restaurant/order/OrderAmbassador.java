package restaurant.order;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import restaurant.Ambassador;
import restaurant.Federate;

public class OrderAmbassador extends Ambassador {

    private OrderFederate orderFederate;

    protected InteractionClassHandle orderExecutionHandle;
    protected InteractionClassHandle placeOrderHandle;



    public OrderAmbassador(OrderFederate orderFederate) {
        this.orderFederate = orderFederate;
    }


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        log("Order Ambassodor received interaction");
    }
}
