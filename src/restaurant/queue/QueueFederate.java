package restaurant.queue;

import hla.rti1516e.CallbackModel;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import restaurant.Federate;
import restaurant.Settings;
import restaurant.customer.Customer;
import restaurant.customer.CustomerFederate;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

public class QueueFederate extends Federate {

    QueueAmbassador fedamb;
    private final double timeStep           = 10.0;



    public void runFederate(String federateName) throws Exception {

        log("Creating RTIambassador");
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log("Connecting...");
        fedamb = new QueueAmbassador(this);
        rtiamb.connect(fedamb, CallbackModel.HLA_EVOKED);


        try {
            URL[] modules = new URL[]{
                    (new File("foms/fom.xml")).toURI().toURL(),

            };

            rtiamb.createFederationExecution("ExampleFederation", modules);
            log("Created Federation");
        } catch (FederationExecutionAlreadyExists exists) {
            log("Didn't create federation, it already existed");
        } catch (MalformedURLException urle) {
            log("Exception loading one of the FOM modules from disk: " + urle.getMessage());
            urle.printStackTrace();
            return;
        }

        URL[] joinModules = new URL[]{};
        rtiamb.joinFederationExecution(federateName, "QueueFederateType", "ExampleFederation", joinModules);

        log("Joined Federation as " + federateName);

        this.timeFactory = (HLAfloat64TimeFactory) rtiamb.getTimeFactory();

        rtiamb.registerFederationSynchronizationPoint(READY_TO_RUN, null);
        // wait until the point is announced
        while (!fedamb.isAnnounced) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        waitForUser();

        rtiamb.synchronizationPointAchieved(READY_TO_RUN);
        log("Achieved sync point: " + READY_TO_RUN + ", waiting for federation...");
        while (!fedamb.isReadyToRun) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }

        enableTimePolicy(fedamb);
        log("Time Policy Enabled");
        publishAndSubscribe();
        log("Published and Subscribed");

        while (fedamb.running) {
            if (fedamb.federateTime > Settings.endSimulationTime) fedamb.running=false;
            Random rnd = new Random();

            double timeToAdvance = fedamb.federateTime + timeStep;
            advanceTime( timeToAdvance ,fedamb);

            double interactionTimeStep = timeToAdvance + fedamb.federateLookahead;

            for (int i = 0; i < rnd.nextInt(Settings.MAX_CUSTOMERS_PER_CYCLE+1); i++) { // 0-3 customers

                log("Some task" + i);
            }
            /* TODO: new interactions to handle
            if(fedamb.externalEvents.size() > 0) { // new interactions to handle
                fedamb.externalEvents.sort(new ExternalEvent.ExternalEventComparator());
                for (ExternalEvent externalEvent : fedamb.externalEvents) {
                    switch (externalEvent.getEventType()) {
                        case START:
                            inServiceList.add(getCarFromQueue(externalEvent.getCarNumber()));
                            break;
                    }
                }
            }*/
        }
    }




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

    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public static void main( String[] args ){
        // get a federate name, use "exampleFederate" as default
        String federateName = "queueFederate";
        if( args.length != 0 ){
            federateName = args[0];
        }

        try{// run the example federate
            new QueueFederate().runFederate( federateName );
        }
        catch(Exception rtie ){
            rtie.printStackTrace();
        }
    }
}
