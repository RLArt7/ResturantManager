package RestBL;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Resturant {
	private String resturantName;
	private int maxCustomersPerDay;
	private int numOfSeats;
//	private int maxNumOfTables;
	private int maxWaitersInShift;
	private int maxCustomersCuncurrentlPerWaiter;
	private int maxNumOfTablesPerWaiter;
	private int maxNumOfWaiters;

	private int customerCunter = 0;
	private LocalDate timeOfOpen;
	private ReentrantLock myRestLock = new ReentrantLock();

	private Vector<Waiter> waiters;
	//	private Queue<Waiter> waitingWaiters;

	private Vector<Waiter> allWaitersInShift;
	private Vector<Waiter> allWaitersFinishedShift;
	private Vector<Table> allTables;

	private Vector<Customer> customers ;
	//	private Queue<Customer> waitingCustomers;
	private Vector<Order> allRestServedAndPayed;
	private Vector<RestaurantListener> allRestarauntListeners;
	private String message;
	private long startTime; 
	private long endTime;

	private Kitchen kitchen;
	private boolean isOpen = false;
	private boolean isOpenForCustomers = false;
	private WorkingDay workingDay;


	public Resturant(String resturantName, int maxCustomersPerDay, int numOfSeats,int maxWaiters,int maxCustomersCuncurrentlPerWaiter/*,Vector<Customer> customers*/) throws SecurityException, IOException {
		this.resturantName = resturantName;
		this.maxCustomersPerDay = maxCustomersPerDay;
		this.maxCustomersCuncurrentlPerWaiter = maxCustomersCuncurrentlPerWaiter;
		this.numOfSeats = numOfSeats;
		this.workingDay = new WorkingDay(resturantName.trim() + "Logger",this);
		this.workingDay.setLoggerLevel(Level.FINEST);
		this.maxWaitersInShift = maxWaiters;
		this.customers = new  Vector<Customer>();
//		this.customers = customers;
		this.kitchen = new Kitchen(this);
		this.allWaitersFinishedShift = new Vector<Waiter>();
		this.allWaitersInShift = new Vector<Waiter>();
		this.allTables = new Vector<Table>();
		this.waiters = new Vector<Waiter>();
		this.allRestServedAndPayed = new Vector<Order>();
		this.allRestarauntListeners = new Vector<RestaurantListener>();
		this.timeOfOpen = LocalDate.now();
		this.isOpen = true;
		setWaitersInfo();
		setAllWaiters();
		setAlltables();



		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	message ,this);
		workingDay.logger().log(Level.INFO, message ,this);
	}
	public void setWaitersInfo(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);

		// According to the max visitors, calculates how many waiters will be needed
		this.maxNumOfWaiters = maxCustomersPerDay /maxCustomersCuncurrentlPerWaiter;
		this.maxNumOfTablesPerWaiter = numOfSeats / maxWaitersInShift ;
		// Adds another waiter if needed
		if(maxCustomersPerDay % maxCustomersCuncurrentlPerWaiter != 0)
			this.maxNumOfWaiters += 1;
		if(numOfSeats % maxWaitersInShift != 0)
			this.maxNumOfTablesPerWaiter += 1;

		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+ " maxNumOfVisiters:" +maxCustomersPerDay+
				" maxCustomersCuncurrentlPerWaiter: " +maxCustomersCuncurrentlPerWaiter+
				" maxNumOfWaiters:" + maxNumOfWaiters ,this);
	}
	private void setAllWaiters() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);
		// Creates the Max num of waiters
		for(int i = 0 ; i < maxNumOfWaiters;i++){
			// Creating a new waiter
			Waiter newWaiter = new Waiter(this);
			// Adds the waiter
			waiters.add(newWaiter);
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message+" add new Waiter,"+newWaiter.toString()+"current number of Waiters,"+waiters.size() ,this);
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message ,this);
	}
	public void setAlltables(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);
		// Creating the tables
		for (int i = 0 ;i < numOfSeats;i++){
			// Adds the table
			allTables.add(new Table(i+1));
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message ,this);
	}
	public void setResturantName(String resturantName) {
		this.resturantName = resturantName;
	}
	public void closeTheDay() {
		// TODO Auto-generated method stub

	}
	public void setMaxWaiters(int maxWaitersPerShift) {
		this.maxWaitersInShift = maxWaitersPerShift;

	}
	public void setMaxCustomersPerWaiter(int maxCustomersCuncurrentlPerWaiter) {
		this.maxCustomersCuncurrentlPerWaiter = maxCustomersCuncurrentlPerWaiter;

	}
	public void addCustomer(Customer customer) {
		//		if(customerCunter < maxCustomersPerDay){
		//			if(customers.size() < numOfSeats){
//		customers.add(customer);


	}
	public LocalDate getTimeOfOpen() {
		return timeOfOpen;
	}

	public Customer[] getWaitingCustomer() {
		return (Customer[]) this.customers.toArray();
	}
	public Vector<Customer> getCustomers() {
		return this.customers;
	}
	public boolean isRunning(){
		return this.isOpen;
	}

	public void notifyallRestaurantListeners(boolean status){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message+" for status,"+status ,this);
		// Going through every rest listener
		for(int i = 0 ; i <allRestarauntListeners.size(); i++){
			// Set the rest status
			allRestarauntListeners.elementAt(i).restStatus(status);
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message+" for status,"+status+" notified to, "+allRestarauntListeners.elementAt(i).toString() ,this);

		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" for status,"+status ,this);
	}

	public boolean isOpenForCustomers() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, "+isOpenForCustomers ,this);

		return isOpenForCustomers;
	}
	public void registerToRestaurantListener(RestaurantListener Listener) {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message+" register new listener, "+Listener.toString() ,this);
		// Add a rest listener
		allRestarauntListeners.add(Listener);
	}
	public int getNumOfVisitors() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, " +customerCunter ,this);

		return this.customerCunter;
	}
	public Vector<Waiter> getWaitersinShift(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, allWaitersInShift" ,this);

		return allWaitersInShift;

	}
	public void Waiterstarter(){// Called from program
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);
		// Going through every waiter
		for(int i = 0 ; i < waiters.size() ; i++){
			// Starts the waiter
			waiters.elementAt(i).start();
			// Writes to log
			workingDay.logger().log(Level.FINEST,">>>>"+message +" Waiter, "+ waiters.elementAt(i).toString() + " started",this);
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message ,this);

	}
	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}
	public double getSumOfBills(){

		double sumOfBill = 0;
		// Going over every paid order
		for(int i = 0 ; i < allRestServedAndPayed.size() ; i ++){
			// Sums the bill
			sumOfBill += allRestServedAndPayed.elementAt(i).getOrderBill();
		}
		return sumOfBill;

	}
	public Vector<Waiter> getAllWaitersInShift() {
		return this.allWaitersInShift;
	}
	public void addWaiter(Waiter aWaiter){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);
		// If there is a need in more waiters
		if (this.waiters.size() < this.maxNumOfWaiters){
			// Adds waiter to waiters vector
			waiters.add(aWaiter);
			// Starts the waiter
			aWaiter.start();
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message+" waiter add," + aWaiter.toString() ,this);

		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message ,this);
	}
	public int getMaxCustomersPerWaiter(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, "+maxCustomersCuncurrentlPerWaiter ,this);

		return maxCustomersCuncurrentlPerWaiter;
	}
	public void  registerWaiterToShift(Waiter aWaiter) throws Exception{
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);
		// Checks if the waiters in shift is not the max number allowed
		if (allWaitersInShift.size() < maxWaitersInShift){

			// Adds the waiter to the shift
			this.allWaitersInShift.add(aWaiter);
			workingDay.logger().log(Level.FINEST,	message +"Waiter,"+ aWaiter.toString()+ " started shift",this);
			// Remove from waiting waiters vector 
			this.waiters.remove(aWaiter);
			workingDay.logger().log(Level.FINEST,	message +"Waiter,"+ aWaiter.toString()+ " removed from allWaiters",this);
			assignTablesToWaiters(aWaiter);
			//this.setOpenForCustomers(true);
			workingDay.logger().log(Level.FINEST,	message +"Waiter,"+ aWaiter.toString()+ "finish sucessfully",this);

		}
		else{
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message +"Waiter,"+ aWaiter.toString()+ " failed",this);
		}
		// If all the waiters are in shift
		if(getAllWaitersInShift().size()==maxWaitersInShift){
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message +"Waiter,"+ aWaiter.toString()+ " in if(getAllWaitersInShift().size()==getMaxWaitersInShift()) ",this);
			// Open the rest to customers
			setOpenForCustomers(true);
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message ,this);
	}
	public void setOpenForCustomers(boolean isOpenForCustomers) throws Exception {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message + " restaurant open for customers, "+isOpenForCustomers ,this);
		// Set the rest to open for customers
		this.isOpenForCustomers = isOpenForCustomers;
		// Notify all waiting customers
		notifyallRestaurantListeners(isOpenForCustomers);

	}
	private void assignTablesToWaiters(Waiter aWaiter) {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message +" for waiter," + aWaiter.toString(),this);
		// Going through every table
		
		for(int i=0; i<allTables.size();i++){
			// If the table is not assigned to a waiter and the waiter can have more tables
			if((!allTables.elementAt(i).getAssignedToWaiter()) && (aWaiter.getTablesSize() < this.maxNumOfTablesPerWaiter )){
				// Adds the table to the waiter
				aWaiter.setTable(allTables.elementAt(i));
				// Writes to log
				workingDay.logger().log(Level.FINEST,	">>>>"+message +" sucessfully add table: "+ allTables.elementAt(i).getNumOfTblID()+" to Waier: " + aWaiter.toString(),this);
			}
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" for waiter," + aWaiter.toString(),this);

	}
	public void setFinishedMyShift(Waiter aWaiter){
		// Writes to log*****************************************change was made here
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message +" for waiter," + aWaiter.toString(),this);
		// Adds waiter to finished shift
		allWaitersInShift.remove(aWaiter);
		allWaitersFinishedShift.add(aWaiter);
		
	}
	public Vector<Waiter> getWaitersThatFinishedShift(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, allWaitersFinishedShift" ,this);
		return allWaitersFinishedShift; 
	}

	public void removeWaiterFromShift(Waiter waiterToRemove) throws Exception {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message +" for waiter," + waiterToRemove.toString(),this);
		// If there are waiters
//		waiterToRemove.setInShift(false);
		if(!waiters.isEmpty()){
			// Runs over every table that waiter to remove has
			for(int i = 0; i < waiterToRemove.getTablesSize(); i++){
				// Adds the tables to the next waiter
				waiters.elementAt(0).setTable(waiterToRemove.getTable(i));
				// Writes to log
				workingDay.logger().log(Level.FINEST,	">>>>"+message +" adding table no, " + waiterToRemove.getTable(i).getNumOfTblID(),this);

			}
			// Remove waiter from shift
			waiterToRemove.setInShift(false);
			// Writes to log
			workingDay.logger().log(Level.FINEST,	">>>>"+message +" for waiter," + waiterToRemove.toString()+ " removed from shift",this);
			// Remove from shift vector
			this.allWaitersInShift.remove(waiterToRemove);
			synchronized(waiters.elementAt(0)){
				// Notify waiter
				waiters.elementAt(0).notify();
				// Writes to log
				workingDay.logger().log(Level.FINEST,	">>>>"+message +" for waiter," + waiters.elementAt(0).toString()+ "have been notified to start shift",this);
			}
		}
		else{
			// Writes to log
			workingDay.logger().log(Level.FINEST,	"There are no more waiters waiting to get into shift",this);
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" for waiter," + waiterToRemove.toString(),this);
	}
	public void setRunning(boolean newStatus){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message +" set is restaurant runing, "+newStatus,this);
		// Set the rest status
		this.isOpen = newStatus;
	}	
	public void regsterCustomerToLine(Customer aCustomer) throws Exception{
		// Writes to log***********************************
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message +" for,"+aCustomer.toString(),this);
		// If the rest has it's max customer per day
		if(this.customerCunter == maxCustomersPerDay){
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message +" for,"+aCustomer.toString() + " at if(NumOfVisitors == maxNumOfVisiters)",this);
			// Set the customer 'canRegister' false
			aCustomer.setCanRegister();
		}
		// If there is room for more customers
		else if ((customerCunter < maxCustomersPerDay) ){

			// Writes to log
			workingDay.logger().log(Level.FINEST,	message +" for,"+aCustomer.toString() + "add to line ",this);
			// Adds customer to waiting line
			customers.add(aCustomer);
			// add cust to rest listener
			registerToRestaurantListener(aCustomer);
			// Set customer 'isRegistered' true
			aCustomer.setIsRegisterder(true);
			// Increase num of visitors by 1
			customerCunter++;
			// Checks if there is a free table to sit the customer
			checkIfCustomerWaitsForTBL();
		}
		// If the rest has reached it's max visitors of the day and all of the customers has paid
		else if((allRestServedAndPayed.size() == maxCustomersPerDay)&&(allRestServedAndPayed.size()>0)) {
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message +" for,"+aCustomer.toString() + " at else if((allRestServedAndPayed.size() == maxNumOfVisiters)&&(allRestServedAndPayed.size()>0)) "+
					"\n\t set open for customers, false" ,this);
			// Close the rest for customers
			setOpenForCustomers(false);

		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message , this);

	}
	public void totalServedAndPayedAdd(Order sarvedAndPayed){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message +" add,"+sarvedAndPayed.toString(),this);
		// Add the Closed order to allRestServedAndPayed
		
		allRestServedAndPayed.add(sarvedAndPayed);

	}
	// Checks if there are free tables
	public void checkIfCustomerWaitsForTBL() throws Exception{
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);
		// Create default Waiter that has the most free tables
		Waiter MaxFreeTblWaiter;
		// Writes to log
		workingDay.logger().log(Level.FINEST,	">>>>"+message +" restaurant allWaitersInShift.size(), " + allWaitersInShift.size() ,this);
		// If there are customers waiting in line and the rest is open for customers
		if(((customers.size() > 0 ) && isOpenForCustomers)){
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message +" at if(((customers.size()>0)&& isOpenForCustomers))" ,this);

			// Gets the first waiter in shift
			if(allWaitersInShift.size()>0){

				// Writes to log
				workingDay.logger().log(Level.FINEST,	message +" 	at if(allWaitersInShift.size()>0)" ,this);
				// Sets the first waiter to default Waiter that has the most free tables
				MaxFreeTblWaiter = allWaitersInShift.elementAt(0);
				// Checking for every waiter in shift
				for(int i = 1; i < allWaitersInShift.size() ; i++ ){
					// Checks which waiter has more free tables
					if(allWaitersInShift.elementAt(i).getNumOfFreeTbl()>MaxFreeTblWaiter.getNumOfFreeTbl()){

						// Set the waiter with more free tables
						MaxFreeTblWaiter = allWaitersInShift.elementAt(i);
						// Writes to log
						workingDay.logger().log(Level.FINEST,	message +" 	at if(allWaitersInShift.elementAt(i).getNumOfFreeTbl()>MaxFreeTblWaiter.getNumOfFreeTbl()" ,this);
						workingDay.logger().log(Level.FINEST,	message + "set MaxFreeTblWaiter as," + allWaitersInShift.elementAt(0).toString() ,this);

					}
				}	
				// Writes to log
				workingDay.logger().log(Level.FINEST,	message+" MaxFreeTblWaiter: " + MaxFreeTblWaiter.getName() + 
						" have the max of free tables: " + MaxFreeTblWaiter.getNumOfFreeTbl()  ,this);


				try{

					// If the chosen waiter has free tables and he hasn't reached his max customers per waiter per day
					if((MaxFreeTblWaiter.getNumOfFreeTbl()>0) && (MaxFreeTblWaiter.getSizeofMyserved() < this.getMaxCustomersPerWaiter())){
						// Writes to log
						workingDay.logger().log(Level.FINEST,	message +" 	at if((MaxFreeTblWaiter.getNumOfFreeTbl()>0) && (MaxFreeTblWaiter.getSizeofMyserved() < this.getMaxCustomersPerWaiter()))" ,this);
						// If there are customers waiting in line
						if(customers.size()>0){

							// Writes to log
							workingDay.logger().log(Level.FINEST,	message +" 	at if(customers.size()>0)" ,this);
							workingDay.logger().log(Level.FINEST,	message  +MaxFreeTblWaiter.getName() + "will get the Customer" + MaxFreeTblWaiter.getNumOfFreeTbl() ,this);
							// Add the customer to the waiter
							MaxFreeTblWaiter.addCustomer(customers.elementAt(0));
							// Writes to log
							workingDay.logger().log(Level.FINEST,	message +" assigned New Customer: "+customers.elementAt(0).getCustName()+
									"for Waiter: "+MaxFreeTblWaiter.getName() ,this);
							// Remove customer from customers waiting in line
							customers.remove(0);
							synchronized(MaxFreeTblWaiter){
								// Notify the waiter
								MaxFreeTblWaiter.notify();
								// Writes to log
								workingDay.logger().log(Level.FINEST,	message +" MaxFreeTblWaiter, "+MaxFreeTblWaiter.toString()+" sucessfully notified" ,this);

							}
						}

					}

//					System.out.println("in here resturant checkIfCustomerWaitsForTBL customers.size(): try4 "+ customers.size());
				}catch(ArrayIndexOutOfBoundsException e){
					// Writes to exception log
					workingDay.logger().log(Level.FINEST,	message +" MaxFreeTblWaiter, "+MaxFreeTblWaiter.toString()+" there is no customer in line" ,this);

				}
			}
			
		}
		
		// If there are no customers waiting in line and the rest have served the max num of customers per day
		else if(customers.isEmpty() && (allRestServedAndPayed.size() == maxCustomersPerDay)){
			// Writes to log
			workingDay.logger().log(Level.FINEST,	message +" at 	else if(customers.isEmpty() && (allRestServedAndPayed.size() == maxNumOfVisiters)) " ,this);
			System.out.println("time to close reach Maximum Client for today");
			isTimeToClose();
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message , this);
	}
	public ReentrantLock getMyRestLock(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, myRestLock "+myRestLock ,this);

		return myRestLock;
	}
	public void showWaitingCustomers(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message ,this);

		// Runs over every customer waiting in line
		for(int i = 0 ; i < customers.size() ; i++){
			// Prints the customer details
			workingDay.logger().log(Level.FINEST,	">>>>"+message +customers.elementAt(i).toString() ,this);
		}
		// Writes to log
		workingDay.logger().log(Level.FINEST,	"<<<<"+message , this);
	}

	public Kitchen getKitchen(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, "+kitchen ,this);

		return this.kitchen;
	}
	public void isTimeToClose(){
		// If there are no customers waiting in line and the rest has served it's max customers to serve per day
		if((allRestServedAndPayed.size() == this.maxCustomersPerDay)&&(customers.size() == 0)){
			// Closing the rest
			this.setOpen(false);
			// Closing the kitchen
			this.kitchen.setOpen(false);
		}
		System.out.println(this.resturantName + " sucessfully finished alone");
		endTime = System.currentTimeMillis();
	}

	public void forcedTimeToClose(){
		// Clearing the waiting customers
		this.customers.clear();
		// Closing the rest
		this.setOpen(false);
		// Closing the kitchen
		this.kitchen.setOpen(false);
		System.out.println(this.resturantName + " sucessfully finished Force");
		endTime = System.currentTimeMillis();
	} 
	public boolean isOpen() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	"<<<<"+message+" return, "+isOpen ,this);

		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		workingDay.logger().log(Level.FINEST,	">>>>"+message+" is open,"+isOpen ,this);
		// Set the new open status
		this.isOpen = isOpen;
	}
	public int getMaxWaitersInShift() {
		return this.maxWaitersInShift;
	}
	@Override
	public String toString() {
		return "Restaurant [name=" + resturantName + "]";
	}




}
