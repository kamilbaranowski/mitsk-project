package restaurant.customer;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import restaurant.Ambassador;
import restaurant.ExternalEvent;
import restaurant.Settings;
import restaurant.order.Order;
import restaurant.table.Table;

import java.util.*;

public class CustomerAmbassador extends Ambassador {

    protected ArrayList<ExternalEvent> externalEvents = new ArrayList<>();

    public CustomerFederate federate;
    //private int keyMapValue = 0;
    private Map<Table, Customer> customerMap = new LinkedHashMap<>();
    private List<String> orderTypes = new ArrayList<>();



    protected InteractionClassHandle enterQueueHandle;
    protected InteractionClassHandle impatientHandle;
    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle placeOrderHandle;
    protected InteractionClassHandle leavingTableHandle;

    protected InteractionClassHandle possibleTakeTableHandle;
    protected InteractionClassHandle startServiceHandle;
    protected InteractionClassHandle endServiceHandle;
    protected InteractionClassHandle paymentHandle;



    public CustomerAmbassador(CustomerFederate federate) {
        this.federate = federate;
        this.orderTypes.add("SOUP");
        this.orderTypes.add("MAIN_COURSE");
        this.orderTypes.add("DESSERT");
        this.orderTypes.add("DRIMG");
    }


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {


        StringBuilder builder = new StringBuilder( "Interaction Received:" );
        double time = theTime!=null?convertTime(theTime):-1;

        if (interactionClass.equals(possibleTakeTableHandle)){
        ParameterHandle tableNumberHandle = null;
        ParameterHandle customerNumberHandle = null;
        try {
            tableNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
            customerNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "customerNumber");
        } catch (NameNotFound nameNotFound) {
            nameNotFound.printStackTrace();
        } catch (InvalidInteractionClassHandle invalidInteractionClassHandle) {
            invalidInteractionClassHandle.printStackTrace();
        } catch (FederateNotExecutionMember federateNotExecutionMember) {
            federateNotExecutionMember.printStackTrace();
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
        }

            int tableNumber = decodeInt(theParameters.get(tableNumberHandle));
            int customerNumber = decodeInt(theParameters.get(customerNumberHandle));

            customerMap.put(new Table(tableNumber), new Customer(customerNumber));
            builder.append("\nCustomer: "+ customerNumber+" taking table with id: " + tableNumber);
            log(builder.toString());
        }
        else if (interactionClass.equals(startServiceHandle)){
            ParameterHandle tableNumberHandle = null;
            Random random = new Random();
            try {
                tableNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
                log("Customer receive: start service interaction from Waiter");
                log("Customer: order dish...");
                wait(100);
                federate.sendPlaceOrderInteraction(orderTypes.get(random.nextInt(4)), (int) (federateTime + random.nextDouble()));
            } catch (NameNotFound nameNotFound) {
                nameNotFound.printStackTrace();
            } catch (InvalidInteractionClassHandle invalidInteractionClassHandle) {
                invalidInteractionClassHandle.printStackTrace();
            } catch (FederateNotExecutionMember federateNotExecutionMember) {
                federateNotExecutionMember.printStackTrace();
            } catch (NotConnected notConnected) {
                notConnected.printStackTrace();
            } catch (RTIinternalError rtIinternalError) {
                rtIinternalError.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SaveInProgress saveInProgress) {
                saveInProgress.printStackTrace();
            } catch (RestoreInProgress restoreInProgress) {
                restoreInProgress.printStackTrace();
            } catch (InvalidLogicalTime invalidLogicalTime) {
                invalidLogicalTime.printStackTrace();
            } catch (InteractionParameterNotDefined interactionParameterNotDefined) {
                interactionParameterNotDefined.printStackTrace();
            } catch (InteractionClassNotPublished interactionClassNotPublished) {
                interactionClassNotPublished.printStackTrace();
            } catch (InteractionClassNotDefined interactionClassNotDefined) {
                interactionClassNotDefined.printStackTrace();
            }
        }
        else if (interactionClass.equals(endServiceHandle)){
            ParameterHandle tableNumberHandle = null;

            try {
                tableNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
                log("Customer receive: end service interaction from Waiter");
                //TODO: trzeba zaimplementowac jakas obsluge tej interakcji oraz interakcji start service
                federate.sendLeavingTableInteraction(decodeInt(theParameters.get(tableNumberHandle)));
            } catch (NameNotFound nameNotFound) {
                nameNotFound.printStackTrace();
            } catch (InvalidInteractionClassHandle invalidInteractionClassHandle) {
                invalidInteractionClassHandle.printStackTrace();
            } catch (FederateNotExecutionMember federateNotExecutionMember) {
                federateNotExecutionMember.printStackTrace();
            } catch (NotConnected notConnected) {
                notConnected.printStackTrace();
            } catch (RTIinternalError rtIinternalError) {
                rtIinternalError.printStackTrace();
            } catch (SaveInProgress saveInProgress) {
                saveInProgress.printStackTrace();
            } catch (RestoreInProgress restoreInProgress) {
                restoreInProgress.printStackTrace();
            } catch (InvalidLogicalTime invalidLogicalTime) {
                invalidLogicalTime.printStackTrace();
            } catch (InteractionParameterNotDefined interactionParameterNotDefined) {
                interactionParameterNotDefined.printStackTrace();
            } catch (InteractionClassNotPublished interactionClassNotPublished) {
                interactionClassNotPublished.printStackTrace();
            } catch (InteractionClassNotDefined interactionClassNotDefined) {
                interactionClassNotDefined.printStackTrace();
            }
        }
        else if (interactionClass.equals(paymentHandle)){
            ParameterHandle amountHandle = null;
            ParameterHandle typeHandle = null;

            try {
                amountHandle = federate.rtiamb.getParameterHandle(interactionClass, "amount");
                typeHandle = federate.rtiamb.getParameterHandle(interactionClass, "type");

                int amount = decodeInt(theParameters.get(amountHandle));
                String type = decodeServiceStand(theParameters.get(typeHandle));
                log("Customer receive payment: amount = " + amount + " type = " + type);
                //TODO: trzeba zaimplementowac obsluge tej interakcji, powinna być odebrana równoczescie z zakonczeniem obslugi???
                log("Custormer pay");
            } catch (NameNotFound nameNotFound) {
                nameNotFound.printStackTrace();
            } catch (InvalidInteractionClassHandle invalidInteractionClassHandle) {
                invalidInteractionClassHandle.printStackTrace();
            } catch (FederateNotExecutionMember federateNotExecutionMember) {
                federateNotExecutionMember.printStackTrace();
            } catch (NotConnected notConnected) {
                notConnected.printStackTrace();
            } catch (RTIinternalError rtIinternalError) {
                rtIinternalError.printStackTrace();
            }
        }
    }

    public Map<Table, Customer> getCustomerMap() {
        return customerMap;
    }

    public Customer removeFirstCustomer() throws Exception {
        Map.Entry<Table, Customer> entry = customerMap.entrySet().iterator().next();
        Table key = entry.getKey();
        Customer value = entry.getValue();
        customerMap.remove(key);
        return value;
    }
}
