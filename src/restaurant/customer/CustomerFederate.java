package restaurant.customer;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import restaurant.Federate;
import restaurant.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class CustomerFederate extends Federate {

    private final double timeStep           = 10.0;
    private CustomerAmbassador fedamb;
    private ArrayList<Customer> customersQueue = new ArrayList<>();


    public void log(String message) {
        System.out.println("CustomerFederate   : " + message);
    }




    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {

        InteractionClassHandle enterQueueInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.enterQueue");
        fedamb.enterQueueHandle = enterQueueInteractionHandle;

        InteractionClassHandle impatientInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.impatient");
        fedamb.impatientHandle = impatientInteractionHandle;

        InteractionClassHandle takingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.takingTable");
        fedamb.takingTableHandle = takingTableInteractionHandle;

        InteractionClassHandle placeOrderInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.placeOrder");
        fedamb.placeOrderHandle = placeOrderInteractionHandle;

        InteractionClassHandle leavingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.leavingTable");
        fedamb.leavingTableHandle = leavingTableInteractionHandle;
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
        rtiamb.publishInteractionClass(impatientInteractionHandle);
        rtiamb.publishInteractionClass(takingTableInteractionHandle);
        rtiamb.publishInteractionClass(placeOrderInteractionHandle);
        rtiamb.publishInteractionClass(leavingTableInteractionHandle);
        //-------------------------------------------------------------
        rtiamb.subscribeInteractionClass(possibleTakeTableInteractionHandle);
        rtiamb.subscribeInteractionClass(startServiceInteractionHandle);
        rtiamb.subscribeInteractionClass(endServiceInteractionHandle);
        rtiamb.subscribeInteractionClass(paymentInteractionHandle);
    }

    public void runFederate( String federateName )  throws Exception {
        log( "Creating RTIambassador" );
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new CustomerAmbassador(this);
        rtiamb.connect( fedamb, CallbackModel.HLA_EVOKED );

        try
        {
            URL[] modules = new URL[]{
                    (new File("foms/fom.xml")).toURI().toURL(),

            };


            rtiamb.createFederationExecution( "ExampleFederation", modules );
            log( "Created Federation" );
        }
        catch( FederationExecutionAlreadyExists exists )
        {
            log( "Didn't create federation, it already existed" );
        }
        catch( MalformedURLException urle )
        {
            log( "Exception loading one of the FOM modules from disk: " + urle.getMessage() );
            urle.printStackTrace();
            return;
        }

        //        URL[] joinModules = new URL[]{};
//        rtiamb.joinFederationExecution( federateName,"CarFederateType" ,"ExampleFederation", joinModules );
        rtiamb.joinFederationExecution( federateName,"CustomerFederateType" ,"ExampleFederation" );

        log( "Joined Federation as " + federateName );

        this.timeFactory = (HLAfloat64TimeFactory)rtiamb.getTimeFactory();

        rtiamb.registerFederationSynchronizationPoint( READY_TO_RUN, null );
        // wait until the point is announced
        while(!fedamb.isAnnounced){
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
        //user should press enter
        waitForUser();

        rtiamb.synchronizationPointAchieved( READY_TO_RUN );
        log( "Achieved sync point: " +READY_TO_RUN+ ", waiting for federation..." );

        while(!fedamb.isReadyToRun)	{
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }

        enableTimePolicy(fedamb);
        log( "Time Policy Enabled" );
        publishAndSubscribe();
        log( "Published and Subscribed" );

        /* ------------ MAIN LOOP ---------------- */

        while (fedamb.running) {
            if (fedamb.federateTime > Settings.endSimulationTime) fedamb.running=false;
            Random rnd = new Random();

            double timeToAdvance = fedamb.federateTime + timeStep;
            advanceTime( timeToAdvance ,fedamb);

            double interactionTimeStep = timeToAdvance + fedamb.federateLookahead;

            for (int i = 0; i < rnd.nextInt(Settings.MAX_CUSTOMERS_PER_CYCLE+1); i++) { // 0-3 customers

            }
        }

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
