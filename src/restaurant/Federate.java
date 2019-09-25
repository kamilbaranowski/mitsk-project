package restaurant;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.RTIexception;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import org.portico.impl.hla1516e.types.time.DoubleTime;
import org.portico.impl.hla1516e.types.time.DoubleTimeInterval;

import java.util.Random;


public class Federate {

    public RTIambassador rtiamb;
    public static final String READY_TO_RUN = "ReadyToRun";
    public HLAfloat64TimeFactory timeFactory; // set when we join
    public EncoderFactory encoderFactory;     // set when we join

    public void log(String message) {
        System.out.println("Federate : " + message);
    }



    public void runFederate( String federateName ) throws Exception
    {

    }


    public double randomTime(double start, double end) {
        double random = new Random().nextDouble();
        return start + (random * (end - start));
    }

    public void enableTimePolicy(Ambassador fedamb) throws Exception
    {

        HLAfloat64Interval lookahead = timeFactory.makeInterval( fedamb.federateLookahead );

        this.rtiamb.enableTimeRegulation( lookahead );

        // tick until we get the callback
        while( fedamb.isRegulating == false )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }

        this.rtiamb.enableTimeConstrained();

        // tick until we get the callback
        while( fedamb.isConstrained == false )
        {
            rtiamb.evokeMultipleCallbacks( 0.1, 0.2 );
        }
    }
    public void advanceTime( double timeToAdvance, Ambassador fedamb) throws hla.rti1516e.exceptions.RTIexception {
        log("requesting time advance for: " + timeToAdvance);
        // request the advance
        fedamb.isAdvancing = true;
        HLAfloat64Time time = timeFactory.makeTime(timeToAdvance);
        rtiamb.timeAdvanceRequest(time);

        // wait for the time advance to be granted. ticking will tell the
        // LRC to start delivering callbacks to the federate
        while (fedamb.isAdvancing) {
            rtiamb.evokeMultipleCallbacks(0.1, 0.2);
        }
    }

    private void publishAndSubscribe() throws RTIexception
    {

    }

    private void sendInteraction() throws RTIexception
    {

    }
    public byte[] generateTag()
    {
        return ("(timestamp) "+System.currentTimeMillis()).getBytes();
    }


    //----------------------------------------------------------
    //                     STATIC METHODS
    //----------------------------------------------------------
    public LogicalTime convertTime(double time ) { // PORTICO SPECIFIC!!
        return new DoubleTime( time );
    }

    public LogicalTimeInterval convertInterval(double time ){ // PORTICO SPECIFIC!!
        return new DoubleTimeInterval( time );
    }




    public static void main( String[] args )
    {
        // get a federate name, use "exampleFederate" as default
        String federateName = "exampleFederate";
        if( args.length != 0 )
        {
            federateName = args[0];
        }

        try
        {
            // run the example federate
            new Federate().runFederate( federateName );
        }
        catch( Exception rtie )
        {
            // an exception occurred, just log the information and exit
            rtie.printStackTrace();
        }
    }
}
