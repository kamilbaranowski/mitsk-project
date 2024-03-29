package restaurant.queue;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import restaurant.Ambassador;
import restaurant.customer.Customer;

import java.util.ArrayList;
import java.util.List;

public class QueueAmbassador extends Ambassador {

    public QueueFederate federate;
    private final int customerQueueSize = 40;
    private List<Customer> customersInQueue = new ArrayList<>();


    protected InteractionClassHandle possibleTakeTableHandle;

    protected InteractionClassHandle enterQueueHandle;
    protected InteractionClassHandle impatientHandle;
    protected InteractionClassHandle tableOccupiedHandle;
    protected InteractionClassHandle tableFreeHandle;


    public QueueAmbassador(QueueFederate federate){
        this.federate = federate;
    }


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        StringBuilder builder = new StringBuilder( "Interaction Received:" );
        double time = theTime!=null?convertTime(theTime):-1;
        ParameterHandle customerNumberHandle = null;

        try {
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

        if (interactionClass.equals(enterQueueHandle)){
            int customerNumber = decodeInt(theParameters.get(customerNumberHandle));
            if (customersInQueue.size() < customerQueueSize) {
                customersInQueue.add(new Customer(customerNumber));
                builder.append("\nCustomer with id: " + customerNumber + " entering the queue.");
            }
            else{
                builder.append("\nQueue is full, customer with id: " + customerNumber + " leaving.");
            }
            log(builder.toString());
        }
        else if (interactionClass.equals(tableFreeHandle)){
            ParameterHandle tableNumberHandle = null;


            try {
                tableNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
                int tableNumber = decodeInt(theParameters.get(tableNumberHandle));
                log("Queue receive table free interaction, with table id: " +tableNumber);
                //TODO: obsluga zwolnienia stolika, tutaj wlasciwie koniec cyklu symulacji
                federate.sendPossibleTakeTableInteraction(tableNumber, 0);
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
    }

    public List<Customer> getCustomersInQueue() {
        return customersInQueue;
    }

    public boolean removeCustomerFromQueue(Customer customer) {
        return customersInQueue.remove(customer);
    }

    public Customer getFirstCustomer(){
        Customer customer = customersInQueue.stream().findFirst().orElseThrow(() -> new NullPointerException("No customers in queue!!"));
        customersInQueue.remove(customer);
        return customer;
    }
}
