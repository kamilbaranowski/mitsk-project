package restaurant;
import hla.rti1516e.*;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.time.HLAfloat64Time;
import org.portico.impl.hla1516e.types.time.DoubleTime;

public abstract class Ambassador extends  NullFederateAmbassador{

    // these variables are accessible in the package
    public double federateTime = 0.0;
    public double grantedTime = 0.0;
    public double federateLookahead = 1.0;
    public boolean isRegulating = false;
    public boolean isConstrained = false;
    public boolean isAdvancing = false;
    public boolean isAnnounced = false;
    public boolean isReadyToRun = false;
    public boolean running = true;

    //----------------------------------------------------------
    //                   INSTANCE VARIABLES
    //----------------------------------------------------------
    private Federate federate;


    //----------------------------------------------------------
    //                      CONSTRUCTORS
    //----------------------------------------------------------

    public Ambassador(Federate federate) {
        this.federate = federate;
    }

    public Ambassador() {
    }



    public void log(String message) {
        System.out.println("FederateAmbassador: " + message);
    }

    public String decodeServiceStand(byte[] bytes) {
        HLAASCIIstring value = null;
        try {
            value = RtiFactoryFactory.getRtiFactory().getEncoderFactory().createHLAASCIIstring();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
        }
        try {
            value.decode(bytes);
        } catch (DecoderException de) {
            return "Decoder Exception: " + de.getMessage();
        }

        return value.getValue();
    }

    public int decodeInt(byte[] bytes) {
        HLAinteger32BE value = null;
        try {
            value = RtiFactoryFactory.getRtiFactory().getEncoderFactory().createHLAinteger32BE();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
        }
        try {
            value.decode(bytes);
        } catch (DecoderException de) {
            de.printStackTrace();
            return 0;
        }
        return value.getValue();
    }



    //////////////////////////////////////////////////////////////////////////
    ////////////////////////// RTI Callback Methods //////////////////////////
    //////////////////////////////////////////////////////////////////////////
    @Override
    public void synchronizationPointRegistrationFailed(String label,
                                                       SynchronizationPointFailureReason reason) {
        log("Failed to register sync point: " + label + ", reason=" + reason);
    }

    @Override
    public void synchronizationPointRegistrationSucceeded(String label) {
        log("Successfully registered sync point: " + label);
    }

    @Override
    public void announceSynchronizationPoint(String label, byte[] tag) {
        log("Synchronization point announced: " + label);
        if (label.equals(Federate.READY_TO_RUN))
            this.isAnnounced = true;
    }

    @Override
    public void federationSynchronized(String label, FederateHandleSet failed) {
        log("Federation Synchronized: " + label);
        if (label.equals(Federate.READY_TO_RUN))
            this.isReadyToRun = true;
    }

    /**
     * The RTI has informed us that time regulation is now enabled.
     */
    @Override
    public void timeRegulationEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isRegulating = true;
    }

    @Override
    public void timeConstrainedEnabled(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.isConstrained = true;
    }

    @Override
    public void timeAdvanceGrant(LogicalTime time) {
        this.federateTime = ((HLAfloat64Time) time).getValue();
        this.grantedTime = ((HLAfloat64Time) time).getValue();
        this.isAdvancing = false;
    }

    public double convertTime(LogicalTime logicalTime) { // PORTICO SPECIFIC!!
        return ((DoubleTime) logicalTime).getTime();
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters,
                                   byte[] tag,
                                   OrderType sentOrdering,
                                   TransportationTypeHandle theTransport,
                                   SupplementalReceiveInfo receiveInfo)
            throws FederateInternalError {
        // just pass it on to the other method for printing purposes
        // passing null as the time will let the other method know it
        // it from us, not from the RTI
        log("received interaction1");
        this.receiveInteraction(interactionClass,
                theParameters,
                tag,
                sentOrdering,
                theTransport,
                null,
                sentOrdering,
                receiveInfo);
    }


    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass,
                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag,
                                   OrderType sentOrdering, TransportationTypeHandle theTransport,
                                   LogicalTime theTime, OrderType receivedOrdering,
                                   SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
    }

}
