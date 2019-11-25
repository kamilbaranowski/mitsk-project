package restaurant.customer;

import hla.rti1516e.InteractionClassHandle;
import restaurant.Ambassador;

public class CustomerAmbassador extends Ambassador {


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
}
