package restaurant.waiter;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import restaurant.Ambassador;
import restaurant.customer.Customer;
import restaurant.order.Order;
import restaurant.table.Table;

import java.util.ArrayList;

public class WaiterAmbassador extends Ambassador {

    protected InteractionClassHandle startServiceHandle;
    protected InteractionClassHandle endServiceHandle;
    protected InteractionClassHandle paymentHandle;


    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle placeOrderHandle;
    protected InteractionClassHandle orderExecutionHandle;


    private WaiterFederate federate;

    private ArrayList<Order> orderList = new ArrayList<>();
    private ArrayList<Table> tableList = new ArrayList<>();

    public WaiterAmbassador(WaiterFederate federate){
        this.federate = federate;
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {


        if (interactionClass.equals(takingTableHandle)){
            log("Waiter Ambassodor received taking table interaction");

            ParameterHandle tableNumberHandle = null;
            ParameterHandle occupiedSeatsHandle = null;

            try {
                tableNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
                occupiedSeatsHandle = federate.rtiamb.getParameterHandle(interactionClass, "occupiedSeats");
                int tableNumber = decodeInt(theParameters.get(tableNumberHandle));
                int ocuupiedSeats = decodeInt(theParameters.get(occupiedSeatsHandle));
                log("Customer take table with id: " + tableNumber + " ocuppied: " + ocuupiedSeats + " seats");
                //TODO: dodac klienta do listy obslugi. Mozliwe ze trzeba bedzie dodac nowy paraetr do fom'a z id klienta
                /*
                1. obsługa po numerze stolika - dodanie do listy stolika, który bedzie obslugiwany przez danego kelnera
                 */
                tableList.add(new Table(tableNumber));
                federate.sendStartServiceInteraction(tableNumber);

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
        else if (interactionClass.equals(placeOrderHandle)){
            ParameterHandle maxRealizationTimeHandle = null;
            ParameterHandle dishHandle = null;

            try {
                maxRealizationTimeHandle = federate.rtiamb.getParameterHandle(interactionClass, "maxRealizationTime");
                dishHandle = federate.rtiamb.getParameterHandle(interactionClass, "dish");
                String dish = decodeServiceStand(theParameters.get(dishHandle));
                int maxRealizationTime = decodeInt(theParameters.get(maxRealizationTimeHandle));
                log("Waiter receive order: Dish: " + dish + " with order realization time: " + maxRealizationTime);
                //TODO: zapisac otrzymane zamowienie
                /*
                1. Dodany do zamównia nr stolika dla, którego jest przygotowywane
                2. Stworzona lista zamówień dla kelnera
                 */
                //Order order = new Order();



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
        else if (interactionClass.equals(orderExecutionHandle)){
            ParameterHandle statusHandle = null;

            try {
                statusHandle = federate.rtiamb.getParameterHandle(interactionClass, "status");

                String status = decodeServiceStand(theParameters.get(statusHandle));
                log("Waiter receive order execution with status: " + status);
                //TODO: musi czekac na koniec obslugi - trzeba pewnie zaimplementowac jakas funkcjie wait ale w federacie
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
}
