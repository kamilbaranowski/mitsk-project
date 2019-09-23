package restaurant.table;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.exceptions.*;
import restaurant.Federate;
import restaurant.customer.CustomerAmbassador;

public class TableFederate extends Federate {

    private TableAmbassador fedamb;

    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {

        InteractionClassHandle freeTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.freeTable");
        fedamb.freeTableHandle = freeTableInteractionHandle;

        InteractionClassHandle occupiedTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.occupiedTable");
        fedamb.occupiedTableHandle = occupiedTableInteractionHandle;

        //-------------------------------------------------------------------------------------------------------------------------

        InteractionClassHandle takingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.takingTable");
        fedamb.takingTableHandle = takingTableInteractionHandle;

        InteractionClassHandle leaveTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.leaveTable");
        fedamb.leaveTableHandle = leaveTableInteractionHandle;



        rtiamb.publishInteractionClass(freeTableInteractionHandle);
        rtiamb.publishInteractionClass(occupiedTableInteractionHandle);

        //-------------------------------------------------------------
        rtiamb.subscribeInteractionClass(takingTableInteractionHandle);
        rtiamb.subscribeInteractionClass(leaveTableInteractionHandle);

    }
}
