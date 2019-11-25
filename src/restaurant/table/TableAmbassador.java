package restaurant.table;

import hla.rti1516e.InteractionClassHandle;
import restaurant.Ambassador;
import restaurant.customer.CustomerFederate;

public class TableAmbassador extends Ambassador {

    public TableFederate federate;
    protected InteractionClassHandle tableFreeHandle;
    protected InteractionClassHandle tableOccupiedHandle;


    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle leavingTableHandle;




}
