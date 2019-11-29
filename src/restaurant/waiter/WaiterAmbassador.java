package restaurant.waiter;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import restaurant.Ambassador;

public class WaiterAmbassador extends Ambassador {

    protected InteractionClassHandle startServiceHandle;
    protected InteractionClassHandle endServiceHandle;
    protected InteractionClassHandle paymentHandle;


    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle placeOrderHandle;
    protected InteractionClassHandle orderExecutionHandle;


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        log("Waiter Ambassodor received interaction");
    }
}
