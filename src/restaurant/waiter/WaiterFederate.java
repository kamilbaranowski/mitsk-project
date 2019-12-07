package restaurant.waiter;

import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.time.HLAfloat64Time;
import restaurant.Federate;

public class WaiterFederate extends Federate {

    private WaiterAmbassador fedamb;

    private void publishAndSubscribe() throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, RestoreInProgress, SaveInProgress {

        InteractionClassHandle startServiceInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.startService");
        fedamb.startServiceHandle = startServiceInteractionHandle;

        InteractionClassHandle endServiceInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.endService");
        fedamb.endServiceHandle = endServiceInteractionHandle;

        InteractionClassHandle paymentInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.payment");
        fedamb.paymentHandle = paymentInteractionHandle;

        //-------------------------------------------------------------------------------------------------------------------------

        InteractionClassHandle takingTableInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.takingTable");
        fedamb.takingTableHandle = takingTableInteractionHandle;

        InteractionClassHandle placeOrderInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.placeOrder");
        fedamb.placeOrderHandle = placeOrderInteractionHandle;

        InteractionClassHandle orderExecutionInteractionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.orderExecution");
        fedamb.orderExecutionHandle = orderExecutionInteractionHandle;



        rtiamb.publishInteractionClass(startServiceInteractionHandle);
        rtiamb.publishInteractionClass(endServiceInteractionHandle);
        rtiamb.publishInteractionClass(paymentInteractionHandle);

        //-------------------------------------------------------------
        rtiamb.subscribeInteractionClass(takingTableInteractionHandle);
        rtiamb.subscribeInteractionClass(placeOrderInteractionHandle);
        rtiamb.subscribeInteractionClass(orderExecutionInteractionHandle);

    }

    public void sendEndServiceInteraction(int tableNumber) throws NotConnected, FederateNotExecutionMember, NameNotFound, RTIinternalError, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);

        HLAinteger32BE tableNumberValue = encoderFactory.createHLAinteger32BE(tableNumber);


        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.endService");

        ParameterHandle tableNumberHandle = rtiamb.getParameterHandle( interactionHandle,"tableNumber" );

        parameters.put(tableNumberHandle, tableNumberValue.toByteArray());


        log("Sending, end service with table id: " + tableNumber);
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
    }


    public void sendStartServiceInteraction(int tableNumber) throws NameNotFound, NotConnected, RTIinternalError, FederateNotExecutionMember, InvalidInteractionClassHandle, SaveInProgress, RestoreInProgress, InteractionClassNotPublished, InteractionClassNotDefined, InvalidLogicalTime, InteractionParameterNotDefined {
        ParameterHandleValueMap parameters = rtiamb.getParameterHandleValueMapFactory().create(0);
        //nrStolika tableNumber
        HLAinteger32BE tableNumberValue = encoderFactory.createHLAinteger32BE(tableNumber);


        InteractionClassHandle interactionHandle = rtiamb.getInteractionClassHandle("HLAinteractionRoot.startService");

        ParameterHandle tableNumberHandle = rtiamb.getParameterHandle( interactionHandle,"tableNumber" );

        parameters.put(tableNumberHandle, tableNumberValue.toByteArray());


        log("Sending, start service with table id: " + tableNumber);
        HLAfloat64Time time = timeFactory.makeTime( fedamb.federateTime+fedamb.federateLookahead );
        rtiamb.sendInteraction( interactionHandle, parameters, generateTag(), time );
    }
}
