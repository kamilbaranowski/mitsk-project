package restaurant.table;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.exceptions.*;
import restaurant.Federate;
import restaurant.customer.CustomerAmbassador;

public class TableFederate extends Federate {

    private TableAmbassador fedamb;

    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {

        InteractionClassHandle tableFreeInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableFree");
        fedamb.tableFreeHandle = tableFreeInteractionHandle;

        InteractionClassHandle tableOccupiedInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableOccupied");
        fedamb.tableOccupiedHandle = tableOccupiedInteractionHandle;

        //-------------------------------------------------------------------------------------------------------------------------

        InteractionClassHandle takingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.takingTable");
        fedamb.takingTableHandle = takingTableInteractionHandle;

        InteractionClassHandle leavingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.leavingTable");
        fedamb.leavingTableHandle = leavingTableInteractionHandle;



        rtiamb.publishInteractionClass(tableFreeInteractionHandle);
        rtiamb.publishInteractionClass(tableOccupiedInteractionHandle);

        //-------------------------------------------------------------
        rtiamb.subscribeInteractionClass(takingTableInteractionHandle);
        rtiamb.subscribeInteractionClass(leavingTableInteractionHandle);

    }
}
