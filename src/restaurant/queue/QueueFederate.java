package restaurant.queue;

import hla.rti1516e.*;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import restaurant.Federate;
import restaurant.Settings;
import restaurant.customer.Customer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class QueueFederate extends Federate {

    QueueAmbassador fedamb;
    private final double timeStep           = 10.0;
    private final int avalibleTablesInRestaurant = 50;
    private List<Customer> customersInRestaurant = new ArrayList<>();

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

            if (!fedamb.getCustomersInQueue().isEmpty()){
                if (customersInRestaurant.size() < avalibleTablesInRestaurant) {
                    Customer customer = fedamb.getFirstCustomer();
                    /*int freeTableIndex = customersInRestaurant.stream()
                            .filter((x)-> x.equals(false))
                            .collect(Collectors.toList())
                            .indexOf(false);
                    customersInRestaurant.add(freeTableIndex, true);
                    fedamb.removeCustomerFromQueue(customer);
                    log("\nCustomer: " + customer.getCustomerNumber() + " entering to the restaurant");
                    //interakcja mozna zajac stolik*/

                    customersInRestaurant.add(customer);
                    sendPossibleTakeTableInteraction(customersInRestaurant.size(), customer.getCustomerNumber());
                }
                else{
                    log("\nNo free seats in restaurant!");
                }
            }
            else{
                log("\nNo Customers in queue");
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


    public void sendPossibleTakeTableInteraction(int freeTableNumber, int customerNumber) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);

        HLAinteger32BE tableNumberValue = encoderFactory.createHLAinteger32BE(freeTableNumber);
        HLAinteger32BE customerNumberValue = encoderFactory.createHLAinteger32BE(customerNumber);

        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.possibleTakeTable");

        ParameterHandle tableNumberHandle = rtiamb.getParameterHandle( interactionHandle,"tableNumber" );
        ParameterHandle customerNumberHandle = rtiamb.getParameterHandle( interactionHandle,"customerNumber" );
        parameters.put(tableNumberHandle, tableNumberValue.toByteArray());
        parameters.put(customerNumberHandle, customerNumberValue.toByteArray());

        log("Sending, take table with id: " + freeTableNumber + " to Customer: " + customerNumber);
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
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