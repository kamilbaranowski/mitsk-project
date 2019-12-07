package restaurant.order;

import hla.rti.FederationExecutionAlreadyExists;
import hla.rti1516e.*;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import restaurant.Federate;
import restaurant.Settings;
import restaurant.customer.CustomerAmbassador;
import restaurant.queue.QueueFederate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class OrderFederate extends Federate{

    private OrderAmbassador fedamb;
    private final double timeStep = 10.0;

    private int orderIterator;

    public void log(String message) {
        System.out.println(message);
    }

    private Order getOrderFromQueue(int orderNumber) {
        Order order = null;
        return order;
    }

    public void sendOrderExecutionInteraction(String status) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);


        HLAASCIIstring statusValue = encoderFactory.createHLAASCIIstring(status);
        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.orderExecution");

        ParameterHandle statusHandle = rtiamb.getParameterHandle( interactionHandle,"status" );

        parameters.put(statusHandle, statusValue.toByteArray());

        log("Sending, order execution interaction, with status: " + status) ;
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
    }


    public void runFederate( String federateName )  throws Exception {
        log( "Creating RTIambassador" );
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new OrderAmbassador(this);
        rtiamb.connect( fedamb, CallbackModel.HLA_EVOKED );

        try
        {
            URL[] modules = new URL[]{
                    (new File("foms/fom.xml")).toURI().toURL(),

            };


            rtiamb.createFederationExecution( "ExampleFederation", modules );
            log( "Created Federation" );
        }
        catch( hla.rti1516e.exceptions.FederationExecutionAlreadyExists exists )
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

        while (fedamb.running) {
            if (fedamb.federateTime > Settings.endSimulationTime) fedamb.running=false;
        }


    }


    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {
        InteractionClassHandle orderExecutionInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.orderExecution");
        fedamb.orderExecutionHandle = orderExecutionInteractionHandle;

        //-----------------------------------------------------------------------------------------

        InteractionClassHandle placeOrderInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.placeOrder");
        fedamb.placeOrderHandle = placeOrderInteractionHandle;




        rtiamb.publishInteractionClass(orderExecutionInteractionHandle);

        //------------------------------------------------------------------
        rtiamb.subscribeInteractionClass(placeOrderInteractionHandle);



    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main( String[] args ){
        // get a federate name, use "exampleFederate" as default
        String federateName = "orderFederate";
        if( args.length != 0 ){
            federateName = args[0];
        }

        try{// run the example federate
            new OrderFederate().runFederate( federateName );
        }
        catch(Exception rtie ){
            rtie.printStackTrace();
        }
    }
}
