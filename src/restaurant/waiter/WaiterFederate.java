package restaurant.waiter;

import hla.rti1516e.*;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import restaurant.Federate;
import restaurant.Settings;
import restaurant.order.OrderAmbassador;
import restaurant.queue.QueueFederate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class WaiterFederate extends Federate {

    private WaiterAmbassador fedamb;


    public void runFederate( String federateName )  throws Exception {
        log( "Creating RTIambassador" );
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new WaiterAmbassador(this);
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
        rtiamb.joinFederationExecution( federateName,"WaiterFederateType" ,"ExampleFederation" );

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

        //TODO: główna pętla kelnera - kelner nic nie robi w tej chwili (w tej postaci czeka na koniec czasu symulacyjnego)
        while (fedamb.running) {
            if (fedamb.federateTime > Settings.endSimulationTime) fedamb.running=false;
        }


    }


    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {

        InteractionClassHandle startServiceInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.startService");
        fedamb.startServiceHandle = startServiceInteractionHandle;

        InteractionClassHandle endServiceInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.endService");
        fedamb.endServiceHandle = endServiceInteractionHandle;

        InteractionClassHandle paymentInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.payment");
        fedamb.paymentHandle = paymentInteractionHandle;

        //-------------------------------------------------------------------------------------------------------------------------

        InteractionClassHandle takingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.takingTable");
        fedamb.takingTableHandle = takingTableInteractionHandle;

        InteractionClassHandle placeOrderInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.placeOrder");
        fedamb.placeOrderHandle = placeOrderInteractionHandle;

        InteractionClassHandle orderExecutionInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.orderExecution");
        fedamb.orderExecutionHandle = orderExecutionInteractionHandle;



        rtiamb.publishInteractionClass(startServiceInteractionHandle);
        rtiamb.publishInteractionClass(endServiceInteractionHandle);
        rtiamb.publishInteractionClass(paymentInteractionHandle);

        //-------------------------------------------------------------
        rtiamb.subscribeInteractionClass(takingTableInteractionHandle);
        rtiamb.subscribeInteractionClass(placeOrderInteractionHandle);
        rtiamb.subscribeInteractionClass(orderExecutionInteractionHandle);

    }

    public void sendPaymentInteraction(int amount, String type) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);

        HLAinteger32BE amountValue = encoderFactory.createHLAinteger32BE(amount);
        HLAASCIIstring typeValue = encoderFactory.createHLAASCIIstring(type);


        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.payment");

        ParameterHandle amountHandle = rtiamb.getParameterHandle( interactionHandle,"amount" );
        ParameterHandle typeHandle = rtiamb.getParameterHandle( interactionHandle,"type" );

        parameters.put(amountHandle, amountValue.toByteArray());
        parameters.put(typeHandle, typeValue.toByteArray());


        log("Sending, payment, amount: " + amount + " type: " + type);
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
    }

    public void sendEndServiceInteraction(int tableNumber) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);

        HLAinteger32BE tableNumberValue = encoderFactory.createHLAinteger32BE(tableNumber);


        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.endService");

        ParameterHandle tableNumberHandle = rtiamb.getParameterHandle( interactionHandle,"tableNumber" );

        parameters.put(tableNumberHandle, tableNumberValue.toByteArray());


        log("Sending, end service with table id: " + tableNumber);
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
    }


    public void sendStartServiceInteraction(int tableNumber) throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        //nrStolika tableNumber
        HLAinteger32BE tableNumberValue = encoderFactory.createHLAinteger32BE(tableNumber);


        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.startService");

        ParameterHandle tableNumberHandle = rtiamb.getParameterHandle( interactionHandle,"tableNumber" );

        parameters.put(tableNumberHandle, tableNumberValue.toByteArray());


        log("Sending, start service with table id: " + tableNumber);
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
    }

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main( String[] args ){
        // get a federate name, use "exampleFederate" as default
        String federateName = "waiterFederate";
        if( args.length != 0 ){
            federateName = args[0];
        }

        try{// run the example federate
            new WaiterFederate().runFederate( federateName );
        }
        catch(Exception rtie ){
            rtie.printStackTrace();
        }
    }
}
