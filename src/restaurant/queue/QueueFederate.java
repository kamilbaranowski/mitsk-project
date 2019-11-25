package restaurant.queue;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.exceptions.*;
import restaurant.Federate;

public class QueueFederate extends Federate {

    QueueAmbassador fedamb;

    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {
        InteractionClassHandle possibleTakeTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.possibleTakeTable");
        fedamb.possibleTakeTableHandle = possibleTakeTableInteractionHandle;

        //-----------------------------------------------------------------------------------------

        InteractionClassHandle enterQueueInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.enterQueue");
        fedamb.enterQueueHandle = enterQueueInteractionHandle;

        InteractionClassHandle impatientInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.impatient");
        fedamb.impatientHandle = impatientInteractionHandle;

        InteractionClassHandle tableOccupiedInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableOccupied");
        fedamb.tableOccupiedHandle = tableOccupiedInteractionHandle;

        InteractionClassHandle tableFreeInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableFree");
        fedamb.tableFreeHandle = tableFreeInteractionHandle;



        rtiamb.publishInteractionClass(possibleTakeTableInteractionHandle);

        //------------------------------------------------------------------
        rtiamb.subscribeInteractionClass(enterQueueInteractionHandle);
        rtiamb.subscribeInteractionClass(impatientInteractionHandle);
        rtiamb.subscribeInteractionClass(tableOccupiedInteractionHandle);
        rtiamb.subscribeInteractionClass(tableFreeInteractionHandle);


    }
}
