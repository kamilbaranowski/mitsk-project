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
    }
}
