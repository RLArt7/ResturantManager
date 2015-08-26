import java.util.Vector;

public class Table {
	private int tableNumber;
	private Waiter waiter;
	private Vector<Customer> customersInTable;
	
	private boolean isTaken;
	
	
	public Table(Waiter waiter, Vector<Customer> customersInTable, int tableNumber) {
		this.waiter = waiter;
		this.customersInTable = customersInTable;
		this.tableNumber = tableNumber;
		this.isTaken = true;
	}
	public boolean isTaken(){
		return this.isTaken;
	}
	public void resetTable(){
		isTaken = false;
		customersInTable.removeAll(customersInTable);
	}
	public Waiter getWaiter() {
		return waiter;
	}
	public Vector<Customer> getCustomersInTable() {
		return customersInTable;
	}
	public int getTableNumber() {
		return tableNumber;
	}
	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}
	public void setCustomersInTable(Vector<Customer> customersInTable) {
		this.customersInTable = customersInTable;
	}
	public void setTableNumber(int tableNumber) {
		this.tableNumber = tableNumber;
	}
	
	@Override
	public String toString() {
		return "Table #: " + tableNumber + ", waiter name: " + waiter.getName() 
		+ ", num of customers in table: " + customersInTable.size()
				+ ", isTaken: " + isTaken + ":";
	}
	
	
	
}
