package restaurant;

import hla.rti1516e.*;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import org.portico.impl.hla1516e.types.time.DoubleTime;
import org.portico.impl.hla1516e.types.time.DoubleTimeInterval;


public class Federate {

    public RTIambassador rtiamb;
    public static final String READY_TO_RUN = "ReadyToRun";
    public HLAfloat64TimeFactory timeFactory; // set when we join
    public EncoderFactory encoderFactory;     // set when we join



    public void runFederate( String federateName ) throws Exception
    {

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
