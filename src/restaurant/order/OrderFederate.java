package restaurant.order;

import hla.rti.FederationExecutionAlreadyExists;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.RtiFactoryFactory;
import restaurant.Federate;

import java.io.File;
import java.net.MalformedURLException;

public class OrderFederate extends Federate {

    private OrderAmbassador fedOrderAmb;
    private final double timeStep = 10.0;

    private int orderIterator;

    public void log(String message) {
        System.out.println(message);
    }

    private Order getOrderFromQueue(int orderNumber) {
        Order order = null;
        return order;
    }

    @Override
    public void runFederate(String federateName) throws Exception {
        log("Cretaing RTIAmbassador");
        rtiamb = RtiFactoryFactory.getRtiFactory().getRtiAmbassador();
        encoderFactory = RtiFactoryFactory.getRtiFactory().getEncoderFactory();

        log("Connecting ... ");
        fedOrderAmb = new OrderAmbassador(this);
        rtiamb.connect(fedOrderAmb, CallbackModel.HLA_EVOKED);

        try
        {
            File fom = new File( "foms/fom.fed" );
            rtiamb.createFederationExecution( "ExampleFederation",
                    fom.toURI().toURL() );
            log( "Created Federation" );
        } catch( MalformedURLException urle )
        {
            log( "Exception processing fom: " + urle.getMessage() );
            urle.printStackTrace();
            return;
        }

        rtiamb.joinFederationExecution( federateName,"CarFederateType" ,"ExampleFederation" );



    }
}
