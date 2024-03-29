package restaurant.order;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import restaurant.Ambassador;
import restaurant.Federate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderAmbassador extends Ambassador {

    private OrderFederate federate;

    protected InteractionClassHandle orderExecutionHandle;
    protected InteractionClassHandle placeOrderHandle;

    private List<Order> orderList = new ArrayList<>();

    public OrderAmbassador(OrderFederate orderFederate) {
        this.federate = orderFederate;
    }


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        log("Order Ambassodor received interaction");

        if (interactionClass.equals(placeOrderHandle)) {
            ParameterHandle maxRealizationTimeHandle = null;
            ParameterHandle dishHandle = null;
            Random random = new Random();
            try {
                maxRealizationTimeHandle = federate.rtiamb.getParameterHandle(interactionClass, "maxRealizationTime");
                dishHandle = federate.rtiamb.getParameterHandle(interactionClass, "dish");
                String dish = decodeServiceStand(theParameters.get(dishHandle));
                int maxRealizationTime = decodeInt(theParameters.get(maxRealizationTimeHandle));
                log("OrderAmbassador receive order: Dish: " + dish + " with order realization time: " + maxRealizationTime);
                //TODO: zapisac otrzymane zamowienie
                Order order = new Order(random.nextInt() ,Order.orderType.valueOf(dish), maxRealizationTime);
                orderList.add(order);
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
