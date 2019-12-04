package restaurant.table;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;
import restaurant.Ambassador;
import restaurant.Federate;
import restaurant.customer.CustomerFederate;

public class TableAmbassador extends Ambassador {

    public TableFederate federate;
    protected InteractionClassHandle tableFreeHandle;
    protected InteractionClassHandle tableOccupiedHandle;

    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle leavingTableHandle;


    public TableAmbassador(Federate federate) {
        super(federate);
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        log("Table Ambassodor received interaction");
    }



}
