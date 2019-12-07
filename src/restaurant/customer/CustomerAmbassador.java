package restaurant.customer;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import restaurant.Ambassador;
import restaurant.ExternalEvent;
import restaurant.table.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CustomerAmbassador extends Ambassador {

    protected ArrayList<ExternalEvent> externalEvents = new ArrayList<>();

    public CustomerFederate federate;
    //private int keyMapValue = 0;
    private Map<Table, Customer> customerMap = new LinkedHashMap<>();


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

            try {
                tableNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
                log("Customer receive: start service interaction from Waiter");

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
