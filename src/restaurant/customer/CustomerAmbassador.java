package restaurant.customer;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import restaurant.Ambassador;
import restaurant.ExternalEvent;

import java.util.ArrayList;

public class CustomerAmbassador extends Ambassador {

    protected ArrayList<ExternalEvent> externalEvents = new ArrayList<>();

    public CustomerFederate federate;
    protected InteractionClassHandle enterQueueHandle;
    protected InteractionClassHandle impatientHandle;
    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle placeOrderHandle;
    protected InteractionClassHandle leavingTableHandle;

    protected InteractionClassHandle possibleTakeTableHandle;
    protected InteractionClassHandle startServiceHandle;
    protected InteractionClassHandle endServiceHandle;
    protected InteractionClassHandle paymentHandle;



    public CustomerAmbassador(CustomerFederate federate) {
        this.federate = federate;
    }


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        log("Interaction received @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ blah");
    }
}
