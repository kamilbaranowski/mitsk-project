package restaurant.table;

import hla.rti1516e.InteractionClassHandle;
import restaurant.Ambassador;
import restaurant.customer.CustomerFederate;

public class TableAmbassador extends Ambassador {

    public TableFederate federate;
    protected InteractionClassHandle freeTableHandle;
    protected InteractionClassHandle occupiedTableHandle;


    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle leaveTableHandle;




}
