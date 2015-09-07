package RestBL;
import java.util.Vector;

public class Table {

	private int numOfTblID;
	private int numOfSeats;
	private boolean isTaken = false;
	private boolean assignedToWaiter= false ;
	
	public Table(int numOfTbl) {
		// Table contractor
		this.numOfTblID = numOfTbl;
	}
	public void setAssignedToWaiter(boolean status){
		this.assignedToWaiter = status;
	}
	public boolean getAssignedToWaiter(){
		return assignedToWaiter;
		
	}

	public int getNumOfSeats() {
		return numOfSeats;
	}
	@Override
	public String toString() {
		// Returns the table info as a string
		return "Table [numOfTblID=" + numOfTblID + "]";
	}

	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}

	public boolean getIsTaken() {
		return isTaken;
	}

	public void setTaken(boolean isTaken) {
		this.isTaken = isTaken;
	}
	public int getNumOfTblID(){
		return this.numOfTblID;
	}
	
	
	
	
}
