import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class Resturant {
	private String resturantName;
	private int maxCustomersPerDay;
	private int numOfSeats;
	private int maxWaitersPerShift;
	private int maxCustomersCuncurrentlPerWaiter;
	private static int customerCunter=0;
	
	private Vector<Waiter> waiters = new  Vector<Waiter>();
	private Queue<Waiter> waitingWaiters = new  LinkedList<Waiter>();
	public Vector<Customer> customers = new  Vector<Customer>();
	private Queue<Customer> waitingCustomers = new  LinkedList<Customer>();
	private Kitchen kitchen;
	private WorkingDay workingDay;

	
	public Resturant(String resturantName, int maxCustomersPerDay, int numOfSeats) throws SecurityException, IOException {
		this.resturantName = resturantName;
		this.maxCustomersPerDay = maxCustomersPerDay;
		this.numOfSeats = numOfSeats;
		workingDay = new WorkingDay(resturantName + "Logger");
	}
	public void setResturantName(String resturantName) {
		this.resturantName = resturantName;
	}
	public void closeTheDay() {
		// TODO Auto-generated method stub
		
	}
	public void setMaxWaiters(int maxWaitersPerShift) {
		this.maxWaitersPerShift = maxWaitersPerShift;
		
	}
	public void setMaxCustomersPerWaiter(int maxCustomersCuncurrentlPerWaiter) {
		this.maxCustomersCuncurrentlPerWaiter = maxCustomersCuncurrentlPerWaiter;
		
	}
	public void addCustomer(Customer customer) {
		if(customerCunter < maxCustomersPerDay){
			if(customers.size() < numOfSeats){
				customers.add(customer);
				customerCunter++;
			}else{
				waitingCustomers.add(customer);
				customerCunter++;
			}
		}
		
	}
	public void addWaiter() {
		if(waiters.size()<maxWaitersPerShift){
			this.waiters.addElement(new Waiter("Waiter"));
		}else{
			this.waitingWaiters.add(new Waiter("Waiter"));
		}
	}
	public Customer[] getWaitingCustomer() {
		return (Customer[]) this.waitingCustomers.toArray();
	}

}
