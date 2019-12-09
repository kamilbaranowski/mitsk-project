package restaurant.table;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;
import javafx.scene.control.Tab;
import restaurant.Ambassador;
import restaurant.Federate;
import restaurant.Settings;
import restaurant.customer.CustomerFederate;

import java.util.ArrayList;
import java.util.List;

public class TableAmbassador extends Ambassador {

    public TableFederate federate;
    protected InteractionClassHandle tableFreeHandle;
    protected InteractionClassHandle tableOccupiedHandle;

    protected InteractionClassHandle takingTableHandle;
    protected InteractionClassHandle leavingTableHandle;
    List<Table> tableList = new ArrayList<>();

    public TableAmbassador(Federate federate) {
        super(federate);
        for(int i = 0; i < Settings.MAX_TABLE; i++) {
            this.tableList.add(new Table(i+1));
            log("Create table number: " +  (i + 1));
        }
    }

    @Override
    public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters, byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime, OrderType receivedOrdering, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
        log("Table Ambassodor received interaction");
        if(interactionClass.equals(takingTableHandle) || interactionClass.equals(leavingTableHandle)) {
            if(getFirstFreeTable() != null ) {
                int tableNumber = getFirstFreeTable().getTableNumber();
                if(getFirstFreeTable().changeIsFree()) {
                    try {
                        federate.sendTableOccupiedInteraction(tableNumber);
                    } catch (NotConnected notConnected) {
                        notConnected.printStackTrace();
                    } catch (FederateNotExecutionMember federateNotExecutionMember) {
                        federateNotExecutionMember.printStackTrace();
                    } catch (NameNotFound nameNotFound) {
                        nameNotFound.printStackTrace();
                    } catch (RTIinternalError rtIinternalError) {
                        rtIinternalError.printStackTrace();
                    } catch (InvalidInteractionClassHandle invalidInteractionClassHandle) {
                        invalidInteractionClassHandle.printStackTrace();
                    } catch (SaveInProgress saveInProgress) {
                        saveInProgress.printStackTrace();
                    } catch (RestoreInProgress restoreInProgress) {
                        restoreInProgress.printStackTrace();
                    } catch (InteractionClassNotPublished interactionClassNotPublished) {
                        interactionClassNotPublished.printStackTrace();
                    } catch (InteractionClassNotDefined interactionClassNotDefined) {
                        interactionClassNotDefined.printStackTrace();
                    } catch (InvalidLogicalTime invalidLogicalTime) {
                        invalidLogicalTime.printStackTrace();
                    } catch (InteractionParameterNotDefined interactionParameterNotDefined) {
                        interactionParameterNotDefined.printStackTrace();
                    }
                    log("isFree change for table number: " + tableNumber + " to false");
                } else {
                    try {
                        federate.sendTableFreeInteraction(tableNumber);
                    } catch (NotConnected notConnected) {
                        notConnected.printStackTrace();
                    } catch (FederateNotExecutionMember federateNotExecutionMember) {
                        federateNotExecutionMember.printStackTrace();
                    } catch (NameNotFound nameNotFound) {
                        nameNotFound.printStackTrace();
                    } catch (RTIinternalError rtIinternalError) {
                        rtIinternalError.printStackTrace();
                    } catch (InvalidInteractionClassHandle invalidInteractionClassHandle) {
                        invalidInteractionClassHandle.printStackTrace();
                    } catch (SaveInProgress saveInProgress) {
                        saveInProgress.printStackTrace();
                    } catch (RestoreInProgress restoreInProgress) {
                        restoreInProgress.printStackTrace();
                    } catch (InteractionClassNotPublished interactionClassNotPublished) {
                        interactionClassNotPublished.printStackTrace();
                    } catch (InteractionClassNotDefined interactionClassNotDefined) {
                        interactionClassNotDefined.printStackTrace();
                    } catch (InvalidLogicalTime invalidLogicalTime) {
                        invalidLogicalTime.printStackTrace();
                    } catch (InteractionParameterNotDefined interactionParameterNotDefined) {
                        interactionParameterNotDefined.printStackTrace();
                    }
                    log("isFree change for table number: " + tableNumber + " to true ");
                }
                if (interactionClass.equals(leavingTableHandle)){
                    ParameterHandle seatsHandle = null;

                    try {
                        seatsHandle = federate.rtiamb.getParameterHandle(interactionClass, "freedSeats");
                        int seats = decodeInt(theParameters.get(seatsHandle));
                        log("TableAmbassador receive leaving table: Freed seats: " + seats);
                        //TODO: zwolnic miejsca na liscie dostepnych
                        //Done
                        ParameterHandle tableNumberToFreeHandle = federate.rtiamb.getParameterHandle(interactionClass, "tableNumber");
                        int tableNumberToFree = decodeInt(theParameters.get(tableNumberToFreeHandle));
                        for(Table table : tableList ) {
                            if(table.getTableNumber() == tableNumberToFree) {
                                table.setFree(true);
                                federate.sendTableFreeInteraction(table.getTableNumber());
                            }
                        }
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
            }
        }
    }

    public Table getFirstFreeTable() {
        for(Table t : tableList) {
            if(t.isFree() == true) return t;
        }
        return null;
    }

}
