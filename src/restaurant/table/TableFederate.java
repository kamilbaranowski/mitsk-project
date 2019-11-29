package restaurant.table;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.exceptions.*;
import restaurant.Federate;
import restaurant.customer.CustomerAmbassador;
import restaurant.queue.QueueFederate;

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

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main( String[] args ){
        // get a federate name, use "exampleFederate" as default
        String federateName = "tableFederate";
        if( args.length != 0 ){
            federateName = args[0];
        }

        try{// run the example federate
            new TableFederate().runFederate( federateName );
        }
        catch(Exception rtie ){
            rtie.printStackTrace();
        }
    }
}
