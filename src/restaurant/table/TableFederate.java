package restaurant.table;

import hla.rti1516e.*;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import javafx.scene.control.Tab;
import restaurant.Federate;
import restaurant.Settings;
import restaurant.customer.CustomerAmbassador;
import restaurant.queue.QueueFederate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TableFederate extends Federate {

    private TableAmbassador fedamb;

    public void log(String message) {
        System.out.println("TableFederate: " + message);
    }

    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {

        //--------------------------------------- PUBLIKACJE -------------------------------------------------------------------

        InteractionClassHandle tableFreeInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableFree");
        fedamb.tableFreeHandle = tableFreeInteractionHandle;

        InteractionClassHandle tableOccupiedInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableOccupied");
        fedamb.tableOccupiedHandle = tableOccupiedInteractionHandle;

        //---------------------------------------- SUBSKRYBCJE --------------------------------------------------------------------

        InteractionClassHandle takingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.takingTable");
        fedamb.takingTableHandle = takingTableInteractionHandle;

        InteractionClassHandle leavingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.leavingTable");
        fedamb.leavingTableHandle = leavingTableInteractionHandle;

        //---------------------- PUBLIKACJE ---------------------------
        rtiamb.publishInteractionClass(tableFreeInteractionHandle);
        rtiamb.publishInteractionClass(tableOccupiedInteractionHandle);
        //--------------------- SUBSKRYBCJE ----------------------------
        rtiamb.subscribeInteractionClass(takingTableInteractionHandle);
        rtiamb.subscribeInteractionClass(leavingTableInteractionHandle);

    }

    public void sendTableOccupiedInteraction(int tableNumber) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        HLAinteger32BE tableNumberValue = encoderFactory.createHLAinteger32BE(tableNumber);
        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableOccupied");
        ParameterHandle tableNumberHandle = rtiamb.getParameterHandle(interactionHandle, "tableNumber");
        parameters.put(tableNumberHandle, tableNumberValue.toByteArray());
        log("Sending>> Table number " + tableNumber + " is occupied");
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
    }

    public void sendTableFreeInteraction(int tableNumber) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameterHandleValueMap = rtiamb.getParameterHandleValueMapFactory().create(0);
        HLAinteger32BE tableNumberValue = encoderFactory.createHLAinteger32BE(tableNumber);
        InteractionClassHandle interactionClassHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.tableFree");
        ParameterHandle tableNumberHandle = rtiamb.getParameterHandle(interactionClassHandle, "tableNumber");
        parameterHandleValueMap.put(tableNumberHandle, tableNumberValue.toByteArray());
        log("Sending>> Table number " + tableNumber + " is free");
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction(interactionClassHandle, parameterHandleValueMap, generateTag(), time);
    }

    @Override
    public void runFederate(String federateName) throws Exception {
        //super.runFederate(federateName);
        log( "Creating RTIambassador" );
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log( "Connecting..." );
        fedamb = new TableAmbassador(this);
        rtiamb.connect( fedamb, CallbackModel.HLA_EVOKED );

        try {
            URL[] modules = new URL[] {
                    (new File("foms/fom.xml")).toURI().toURL(),
            };
            //rtiamb.createFederationExecution( "ExampleFederation", modules );
            log( "Created Federation" );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        rtiamb.joinFederationExecution(federateName, "TableFederateType", "ExampleFederation");
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

        // ---------------------- GDY STATUS RUNNING ----------------

        while(fedamb.running) {
            log("Into fedamb.running");
            if (fedamb.federateTime > Settings.endSimulationTime) fedamb.running=false;

            double timeToAdvance = fedamb.federateTime + Settings.TIME_STEP;
            advanceTime( timeToAdvance ,fedamb);

            double interactionTimeStep = timeToAdvance + fedamb.federateLookahead;

            List<Table> tables = new ArrayList<>();
            for(int i = 0; i < Settings.MAX_TABLE; i++) {
                tables.add(new Table(i+1));
                log("Create table number: " +  (i + 1));
            }


        }
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
            new TableFederate().runFederate(federateName);
        }
        catch(Exception rtie ){
            rtie.printStackTrace();
        }
    }
}
