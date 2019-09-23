package restaurant.customer;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import restaurant.Federate;
import restaurant.Settings;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class CustomerFederate extends Federate {

    private CustomerAmbassador fedamb;
    private ArrayList<Customer> customersQueue = new ArrayList<>();


    private void log(String message) {
        System.out.println("CarFederate   : " + message);
    }


    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {

        InteractionClassHandle enterQueueInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.enterQueue");
        fedamb.enterQueueHandle = enterQueueInteractionHandle;

        InteractionClassHandle exitQueueInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.exitQueue");
        fedamb.exitQueueHandle = exitQueueInteractionHandle;

        InteractionClassHandle impatientInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.impatient");
        fedamb.impatientHandle = impatientInteractionHandle;

        InteractionClassHandle takingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.takingTable");
        fedamb.takingTableHandle = takingTableInteractionHandle;

        InteractionClassHandle placeOrderInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.placeOrder");
        fedamb.placeOrderHandle = placeOrderInteractionHandle;

        InteractionClassHandle leaveTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.leaveTable");
        fedamb.leaveTableHandle = leaveTableInteractionHandle;
        //-------------------------------------------------------------------------------------------------------------------------

        InteractionClassHandle possibleTakeTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.possibleTakeTable");
        fedamb.possibleTakeTableHandle = possibleTakeTableInteractionHandle;

        InteractionClassHandle startServiceInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.startService");
        fedamb.startServiceHandle = startServiceInteractionHandle;

        InteractionClassHandle endServiceInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.endService");
        fedamb.endServiceHandle = endServiceInteractionHandle;

        InteractionClassHandle paymentInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.payment");
        fedamb.paymentHandle = paymentInteractionHandle;


        rtiamb.publishInteractionClass(enterQueueInteractionHandle);
        rtiamb.publishInteractionClass(exitQueueInteractionHandle);
        rtiamb.publishInteractionClass(impatientInteractionHandle);
        rtiamb.publishInteractionClass(takingTableInteractionHandle);
        rtiamb.publishInteractionClass(placeOrderInteractionHandle);
        rtiamb.publishInteractionClass(leaveTableInteractionHandle);
        //-------------------------------------------------------------
        rtiamb.subscribeInteractionClass(possibleTakeTableInteractionHandle);
        rtiamb.subscribeInteractionClass(startServiceInteractionHandle);
        rtiamb.subscribeInteractionClass(endServiceInteractionHandle);
        rtiamb.subscribeInteractionClass(paymentInteractionHandle);
    }

    public void runFederate( String federateName )  throws Exception {
       //TODO: Implement Customer runFederate method!!!
}

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main( String[] args ){
        // get a federate name, use "exampleFederate" as default
        String federateName = "customerFederate";
        if( args.length != 0 ){
            federateName = args[0];
        }

        try{// run the example federate
            new CustomerFederate().runFederate( federateName );
        }
        catch(Exception rtie ){
            rtie.printStackTrace();
        }
    }
}
