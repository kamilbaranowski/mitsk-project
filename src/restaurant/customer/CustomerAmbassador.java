package restaurant.customer;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import restaurant.Ambassador;
import restaurant.ExternalEvent;

import java.util.ArrayList;

public class CustomerAmbassador extends Ambassador {

    protected ArrayList<ExternalEvent> externalEvents = new ArrayList<>();

    public CustomerFederate federate;
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
        try {
            tableNumberHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
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
            builder.append("\nCustomer taking table with id: " + tableNumber);
            log(builder.toString());
        }
    }
}
