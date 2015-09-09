package RestBL;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;

import RestBL.Order.status;

public class Waiter extends Thread implements WaiterOrderListener{
	private static int id_counter = 0;
	private String name;
	private static int order = 1;
	private  int currentNumOfCustomer;
	private Vector<Table> allMyTabels;
	private Vector<Customer> myServed ;
	private Vector<Order> myServedAndPayed ;
	private Vector<Order> myOrders ;
	private Vector<Order> allMyToDo;
	private int maxCustToServe;
	private int numOfServed = 0 ;
	private Resturant resturant;
	private boolean inShift = false ;
	private boolean notDoneShift = true ;
	private boolean registerdToShift = false;
	private boolean lockedOnRest = false;
	private boolean lockedOnOrder = false;
	private String message;
	WorkingDay waiterLogger;

	public Waiter(Resturant rest) {
		this.name = "Waiter_"+ id_counter++;
		currentNumOfCustomer=0;
		waiterLogger= new WorkingDay(name,this);
		waiterLogger.setLoggerLevel(Level.FINEST);
		this.resturant = rest;
		allMyToDo = new Vector<Order>();
		allMyTabels = new Vector<Table>();
		myServed = new Vector<Customer>();
		myServedAndPayed = new Vector<Order>();
		myOrders = new Vector<Order>();

		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message,this);
		waiterLogger.logger().log(Level.INFO, message ,this);

	}
	@Override
	public String toString() {
		return " Waiter [name=" + name + "]";
	}
	public void addCustomer(Customer aCustomer){
		// Writes to log
		message = ">>>>["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);
		// If the waiter doesn't contain the customer
		if(!myServed.contains(aCustomer)){
			// Adds the customer
			myServed.add(aCustomer);
			// Sits the customer 
			seetCustomerToHisTable(aCustomer);
		}
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);

	}
	public void seetCustomerToHisTable(Customer aCustomer){
		// Writes to log
		message = ">>>>["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);

		Order myOrder = null;
		// Goes through every table
		for(int i = 0 ; i < allMyTabels.size() ; i++){
			// If the table is not taken and the rest is open for customers
			if(!allMyTabels.elementAt(i).getIsTaken() && (this.resturant.isOpenForCustomers())){
				// Set the Chosen table to taken
				allMyTabels.elementAt(i).setTaken(true);
				// Writes to log
				waiterLogger.logger().log(Level.FINEST,	message+ "found aTable: " + allMyTabels.elementAt(i).getNumOfTblID() + "getIsTaken:" +  allMyTabels.elementAt(i).getIsTaken()  ,this);
				// Create an order for the customer
				myOrder = new Order(allMyTabels.elementAt(i),this,resturant.getKitchen(),aCustomer);
				// Writes to log
				waiterLogger.logger().log(Level.FINEST,	message+"seetCustomerToHisTable(); opend new Order, " + myOrder.toString() ,this);
				// Adds the order to the waiters orders
				myOrders.add(myOrder);
				// Set the order to the customer
				aCustomer.setOrder(myOrder);
				// Writes to log
				waiterLogger.logger().log(Level.FINEST, message+"seetCustomerToHisTable(); has sucessfully to seat aCustomer " + aCustomer.getId() ,this);
				// Set the order status
				myOrder.setStatus(status.WaitingMenu);
				
				
				// Stops the for
				break;
			}
		}
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);
	}
	public void giveBill(Order toGiveBill) throws Exception{
		// Writes to log
		message = ">>>>["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST, message +" served the Order: "+toGiveBill.getNumOfOrder() +" bill" ,this);
		//If the waiter is not locked on the order
		if(!lockedOnOrder){
			// Writes to log
			waiterLogger.logger().log(Level.FINEST, this.getClass().getName()+" giveBill(); in while(!lockedOnOrder) Order: "+toGiveBill.getNumOfOrder() ,this);
			try{
				// Writes to log
				waiterLogger.logger().log(Level.FINEST, message+" in while(!lockedOnOrder) Order: "+toGiveBill.getNumOfOrder()+
						" try to get the order locked",this);
				// Tries to lock the order
				lockedOnOrder = toGiveBill.getmyOrderLock().tryLock();

			}finally{
				// If failed to lock the order
				if(!lockedOnOrder)
				{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST, message+ " in while(!lockedOnOrder) Order: "+toGiveBill.getNumOfOrder()+
							" failed to get the order locked",this);
					// Wait and try again
					wait(500);
				}
				else{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST, message +"in while(!lockedOnOrder) Order: "+toGiveBill.getNumOfOrder()+
							" sucessfully order locked",this);
					// Change the order status
					toGiveBill.setStatus(status.Closed);
					// Adds the order to my served and paid orders
					updateMyservedandPayed(toGiveBill);
					// if the order status is closed
					if(toGiveBill.getOrderStatus() == status.Closed){
						// Set the table to not taken
						toGiveBill.getOrderTable().setTaken(false);

					}
					// Unlock the order
					toGiveBill.getmyOrderLock().unlock();
					// Remove the order from the waiters to do
					allMyToDo.remove(toGiveBill);
					// Checks if there is another customer waiting to be sited
					resturant.checkIfCustomerWaitsForTBL();
				}
				// Writes to log
				waiterLogger.logger().log(Level.FINEST, message + "sucessfully deliverd "+toGiveBill.getNumOfOrder()  ,this);
				waiterLogger.logger().log(Level.INFO, message + "sucessfully deliverd "+toGiveBill.getNumOfOrder()  ,this);
			}
		}
		// Set to not locked on order
		this.lockedOnOrder = false;
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);

	}
	public void updateMyservedandPayed(Order toGiveBill){
		// Writes to log
		message = ">>>>["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);
		// Adds the order to the waiters served and paid
		myServedAndPayed.add(toGiveBill);
		// Adds the order to the rest served and paid
		resturant.totalServedAndPayedAdd(toGiveBill);
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);
	}
	public int getSizeofMyserved(){
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message +"return myServed.size(),"+myServed.size() ,this);

		return myServed.size();


	}
	public int getSizeofMyservedAndPayed(){
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	message+" size(),"+myServedAndPayed.size() ,this);

		return myServedAndPayed.size();
	}
	public int getNumOfFreeTbl(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	">>>>"+message ,this);

		int count = 0;
		// Goes through every table
		for(int i = 0 ; i < allMyTabels.size() ; i++){
			// If the table is not taken
			if(!allMyTabels.elementAt(i).getIsTaken()){
				// Increase the count
				count++;
				// Writes to log
				waiterLogger.logger().log(Level.FINEST,	">>>>"+message+"set number of free tables after loop count to: "+ count ,this);
			}
		}
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,	"<<<<"+message+" total number of free tables is: "+ count  ,this);

		return count;
	}
	public void registerToShift() throws InterruptedException{
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	">>>>"+message ,this);
		try {
			// Tries to register to shift
			resturant.registerWaiterToShift(this);
			// Set the waiter's status to in shift
			this.setRegisterdToShift(true);
			// Writes to log
			waiterLogger.logger().log(Level.FINEST,	message+" sucessfully registered: i an here 0" ,this);
		} catch (Exception e) {
			// Writes exception to log
			waiterLogger.logger().log(Level.FINEST,	message+" failed to registered:\n" + e.getMessage(),this);
			waiterLogger.logger().log(Level.FINEST, e.getMessage(),this);
		}

	}
	@Override
	public void  run() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,	">>>>"+message ,this);
		waiterLogger.logger().log(Level.INFO,	">>>>"+message ,this);

		// While the rest is running and the waiter is not in shift
		while(resturant.isRunning()&&(!inShift)){
			// Writes to log
			waiterLogger.logger().log(Level.FINEST,message+" at while(theRest.isRunning()&&(!inShift))" ,this);
			// While the waiter isn't in shift
			while(!inShift){
				// Writes to log
				waiterLogger.logger().log(Level.FINEST,message+" at while(theRest.isRunning()&&(!inShift))"+"\nat while(!inShift)"  ,this);
				try{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST,message+" at while(theRest.isRunning()&&(!inShift))"+"\nat while(!inShift) in try to lock the restaurant"  ,this);
					// Tries to lock the rest
					lockedOnRest = resturant.getMyRestLock().tryLock();
				}finally{
					// If failed to lock the rest
					if(!lockedOnRest){
						// Writes to log
						waiterLogger.logger().log(Level.FINEST,message+" at while(theRest.isRunning()&&(!inShift))"+"\nat while(!inShift) in failed to lock the restaurant"  ,this);
						
						synchronized(this){
							try {
								// Writes to log
								waiterLogger.logger().log(Level.FINEST,message+"\nat while(theRest.isRunning()&&(!inShift))"+"\nat while(!inShift) in failed to lock the restaurant"+"\n going to wait(400)"  ,this);
								// Waits and then try to lock again
								wait(400);

							} catch (InterruptedException e) {
								// Writes exception to log
								waiterLogger.logger().log(Level.FINEST,message+"\nat while(theRest.isRunning()&&(!inShift))"+"\nat while(!inShift) in failed to lock the restaurant"
										+"\n failed to wait(400) "+e.getMessage()  ,this);
							}
						}
					}
					// If locked the rest
					else if (lockedOnRest){
						// Writes to log************************************************************************************************
						waiterLogger.logger().log(Level.FINEST,message+"\nat (!inShift) sucessfully locked the restaurant" ,this);
						try {
							// Register waiter to shift
							registerToShift();
							// If registered
							if(resturant.getAllWaitersInShift().contains(this)){
								// Set the waiter status to in shift
								setInShift(true);
							}
							// Writes to log
							resturant.getMyRestLock().unlock();
						} catch (InterruptedException e) {
							// Writes exception to log
							waiterLogger.logger().log(Level.FINEST,message+"sucessfully loked the restaurant and failed to registered,"+e.getMessage() ,this);
							// If there is no more room for waiters in shift
							if(resturant.getAllWaitersInShift().size() == resturant.getMaxWaitersInShift())
								try {
									// Writes to log
									waiterLogger.logger().log(Level.FINEST,message+"sucessfully loked the restaurant and failed to registered,"
											+"at if(theRest.getAllWaitersInShift().size() == theRest.getMaxWaitersInShift()) try to wait",this);
									// Waits until there is room in shift
									wait();
									// Writes to log
									waiterLogger.logger().log(Level.FINEST,message+"sucessfully loked the restaurant and failed to registered,"
											+"at if(theRest.getAllWaitersInShift().size() == theRest.getMaxWaitersInShift()) notified going out of wait()",this);
								} catch (InterruptedException e1) {
									// Writes exception to log
									waiterLogger.logger().log(Level.FINEST,message+"sucessfully loked the restaurant and failed to registered,"
											+"at if(theRest.getAllWaitersInShift().size() == theRest.getMaxWaitersInShift()) failed to wait(),"+e1.getMessage(),this);
								}
						}
					}
				}

			}
			// Writes to log
			
			waiterLogger.logger().log(Level.FINEST,message+" in while(theRest.isRunning()&&(!inShift)) sucessfully registered" ,this);
		}
		
		// While the waiter is in shift and the rest is open
		while(inShift && resturant.isOpen()){
			// Writes to log
			waiterLogger.logger().log(Level.FINEST,message+" in while(inShift && theRest.isOpen())" ,this);
			//if the waiter has orders to do
			if(allMyToDo.size()>0){
				// Writes to log
				waiterLogger.logger().log(Level.FINEST,message +" in while(inShift) at if(allMyToDo.size()>0) ",this);

				try {
					// Do the first order in line
					doWhatInListLine();


				} catch (InterruptedException e1) {
					// Writes exception to log
					waiterLogger.logger().log(Level.FINEST,message+" in while(inShift) at if(allMyToDo.size()>0) failed doWhatInListLine(); failed," + e1.getMessage() ,this);
				}
			}
			// If there are no orders to do
			else
				synchronized(this){
					try {
						try {
							// Checks if there is a customer waiting to be sited
							resturant.checkIfCustomerWaitsForTBL();
						} catch (Exception e) {
							// Writes exception to log							
							waiterLogger.logger().log(Level.FINEST,message+" in while(inShift) at if(allMyToDo.size()==0)  at theRest.checkIfCustomerWaitsForTBL(); " + e.getMessage() ,this);

						}
						// If the waiter has served the max customers per waiter per shift
						if((myServedAndPayed.size()==resturant.getMaxCustomersPerWaiter())&&(myServedAndPayed.size()!=0)){
							// Writes to log
							waiterLogger.logger().log(Level.FINEST,message+" in while(inShift) at if(allMyToDo.size()==0) at if((myServedAndPayed.size()==theRest.getMaxCustomersPerWaiter()" ,this);
							try {
								// Remove the waiter from shift
								resturant.removeWaiterFromShift(this);
							} catch (Exception e) {
								// Writes exception to log
								waiterLogger.logger().log(Level.FINEST,message+" in while(inShift) at if(allMyToDo.size()==0) at if((myServedAndPayed.size()==theRest.getMaxCustomersPerWaiter()"+
										" failed to remove waiter from shift, "+e.getMessage(),this);
								// Set the waiter's status to 'not in shift'
								this.setInShift(false);

							}
						}
						// If the waiter can serve more customers
						else{
							// Writes to log
							waiterLogger.logger().log(Level.FINEST,message +" in while(inShift)  \n"+
									"maxCustToServe: "+resturant.getMaxCustomersPerWaiter() + " myServedAndPayed.size(): " + myServedAndPayed.size() +  " going to wait(2000) am i here wait??" ,this);
							// Wait for more customers to be added to waiter
							wait(10);
						}
					} catch (InterruptedException e) {
						// Writes exception to log
						waiterLogger.logger().log(Level.FINEST,message+" in while(inShift) at if(allMyToDo.size()==0) failed to go in wait(2000), " +e.getMessage(),this);

					}
				}

		}
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message+" task sucessfully finished",this);
		waiterLogger.logger().log(Level.INFO,"<<<<"+message+" task sucessfully finished",this);

		// Updates the rest that the waiter has finished his shift
		resturant.setFinishedMyShift(this);
		// Close the handler
		
		waiterLogger.theFileHandler().close();

	}
	public void takeOrder(Order toTakeOrder) throws InterruptedException {
		// Writes to log
		message = ">>>>["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +" for "+ toTakeOrder.toString();
		waiterLogger.logger().log(Level.FINEST,	message ,this);
		waiterLogger.logger().log(Level.INFO,	message ,this);
		// If not locked on order
		if(!lockedOnOrder){
			// Writes to log
			waiterLogger.logger().log(Level.FINEST,	message + " at if(!lockedOnOrder) ",this);
			try{
				// Writes to log
				waiterLogger.logger().log(Level.FINEST,	message + " at if(!lockedOnOrder) try to lock ",this);
				// Tries to lock the order
				lockedOnOrder = toTakeOrder.getmyOrderLock().tryLock();

			}finally{
				// If failed to lock the order
				if(!lockedOnOrder)
				{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST,	message + " at if(!lockedOnOrder) failed to get the order locked going to Wait(300) ",this);
					synchronized (this) {
						// Wait 
						wait(300);
					}

				}
				else{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST, message+ "in while(!lockedOnOrder) Customer: "+toTakeOrder.toString(),this);
					// Change the order status to new (Goes to kitchen to make the order)
					toTakeOrder.setStatus(status.New);
					// Remove the order from the waiters to do list
					allMyToDo.remove(toTakeOrder);
					// Unlock the order
					toTakeOrder.getmyOrderLock().unlock();
					// Set the waiter not locked on order
					this.lockedOnOrder = false;
					// Writes to log
					waiterLogger.logger().log(Level.FINEST,"<<<<"+message+ "in while(!lockedOnOrder) Customer: "+toTakeOrder.toString()+" sucessfully made the order",this);

				}

			}
		}

	}
	public String getWaiterName(){
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +" return name,"+name;
		waiterLogger.logger().log(Level.FINEST,	message ,this);
		
		return name;
	}

	public Vector<Customer> getAllmyServed(){
		// Writes to log
		message = "<<<<["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +" return myServed";
		waiterLogger.logger().log(Level.FINEST,	message ,this);
		
		return myServed;
	}
	public void resiveCustomerFromWaiter(Vector<Customer> myCustomers){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,">>>>"+message ,this);
		// Clear the waiters to serve customers
		myServed.clear();
		// If the waiter has customers in the customer vector that was sent
		if(!myCustomers.isEmpty()){
			// Writes to log
			waiterLogger.logger().log(Level.FINEST, message+" at if(!myCustomers.isEmpty()) ",this);
			// Adds the customers to the to serve customers
			myServed = myCustomers;

			// Goes through every customer
			for(int i = 0 ; i <myServed.size() ; i++){
				// Writes to log
				waiterLogger.logger().log(Level.FINEST, message+" at if(!myCustomers.isEmpty()) "
						+" at for(int i = 0 ; i <myServed.size() ; i++)\n "+" for Customer: "+ myServed.elementAt(i).getOrder().getCustomer().getCustName()
						+" for order: "+myServed.elementAt(i).getOrder().getNumOfOrder()	,this);
				// If the customer's order is not closed
				if(myServed.elementAt(i).getOrder().getOrderStatus() != status.Closed){
					// Set the order waiter to this waiter
					myServed.elementAt(i).getOrder().setOrderWaiter(this);
					// Adds the order to the waiters orders
					myOrders.add(myServed.elementAt(i).getOrder());
				}
			}
		}
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message ,this);
	}
	public void setInShift(boolean ShiftStatus){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +" sets his in shift status to: "+ShiftStatus;
		waiterLogger.logger().log(Level.FINEST,">>>>"+message ,this);
		// Sets the waiters shift status
		this.inShift = ShiftStatus;
		this.notDoneShift = ShiftStatus;
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message+" sucessfully" ,this);
		
	}
	public boolean inShift(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +" return,"+inShift;
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message ,this);
		
		return inShift;

	}
	public void doWhatInListLine() throws InterruptedException{
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,">>>>"+message+" have mission to do" ,this);
		// Get the first order in line
		Order newOrderStatus = (Order)allMyToDo.elementAt(0);
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,">>>>"+message+" have mission to do newOrderStatus, " + newOrderStatus.toString() ,this);
		// If the order status is new
		if(newOrderStatus.getOrderStatus() == status.New){
			// Updates the kitchen that it has a new order to make
			resturant.getKitchen().newOrderUpdate(newOrderStatus);
			//Remove the order from the waiters to do
			allMyToDo.remove(0);
		}
		// If the order status is waiting for menu
		else if(newOrderStatus.getOrderStatus() == status.WaitingMenu){
			// Give the menu
			giveMenu(newOrderStatus);
		}
		// If the order status is wait to order
		else if(newOrderStatus.getOrderStatus() == status.WaitForOrder){
			// Goes and takes the order
			takeOrder(newOrderStatus);
		}
		// If the order status is food ready
		else if(newOrderStatus.getOrderStatus() == status.FoodReady){
			// Serve the food
			serveOrder(newOrderStatus);
		}
		// If the order status is waiting for bill
		else if(newOrderStatus.getOrderStatus() == status.WaitingForBill){
			try {
				// Gives bill
				giveBill(newOrderStatus);
			} catch (Exception e) {
				// Writes Exception to log
				waiterLogger.logger().log(Level.FINEST,"<<<<"+message+" have failed to do  " + e.getMessage() ,this);
				
			}
		}
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message+" finished mission" ,this);
	}	

	public Table getTable(int index){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString()+ " return,"+ allMyTabels.elementAt(index).toString();
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message,this);
		
		return allMyTabels.elementAt(index);
	}
	public int getTablesSize(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString()+"return,"+allMyTabels.size();
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message,this);
		
		return allMyTabels.size();
	}
	public void setRegisterdToShift(boolean status){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() + " set status," +status;
		waiterLogger.logger().log(Level.FINEST,">>>>"+message,this);
		// Writes to log
		this.registerdToShift = status;
	}
	public boolean getRegisterdToShift(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString()+" return,"+registerdToShift;
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message,this);
		
		return registerdToShift;

	}
	public void setTable(Table tblToAdd){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,">>>>"+message,this);
		// Adds the table to the waiter's tables
		allMyTabels.add(tblToAdd);
		// Set the table to assigned to waiter
		tblToAdd.setAssignedToWaiter(true);
	}
	public Vector<Order> getMyOrders() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString()+"return all Orders";
		waiterLogger.logger().log(Level.FINEST,">>>>"+message ,this);
		
		return myOrders;
	}
	public void giveMenu(Order newOrder) throws InterruptedException{
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,">>>>"+message +" for " + newOrder.toString(),this);
		// If not locked on order
		if(!lockedOnOrder){
			// Writes to log
			waiterLogger.logger().log(Level.FINEST,">>>>"+message +"  in if(!lockedOnOrder)  for " + newOrder.toString(),this);
			try{
				// Writes to log
				waiterLogger.logger().log(Level.FINEST,">>>>"+message +"  in if(!lockedOnOrder)  for " + newOrder.toString()+" try to get the order locked",this);
				// Try to lock the order
				lockedOnOrder = newOrder.getmyOrderLock().tryLock();
			}finally{
				// If failed to lock the order
				if(!lockedOnOrder)
				{
					synchronized (this) {
						// Writes to log
						waiterLogger.logger().log(Level.FINEST,">>>>"+message +"  in if(!lockedOnOrder)  for " + newOrder.toString()+"in while(!lockedOnOrder) Order: "+newOrder.getNumOfOrder()+
								" failed to get the order locked going to wait(200)",this);
						// Waits
						wait(200);
					}

				}
				// If the order was locked
				else{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST,">>>>"+message +"  in if(!lockedOnOrder)  for " + newOrder.toString()+" locked on order",this);
					// Set the order status to customer is browsing menu
					newOrder.setStatus(status.BrowsingMenu);
					// Set not locked on order
					this.lockedOnOrder = false;
					// Removes the order from the waiter's to do list
					allMyToDo.remove(newOrder);
					// Unlocks the order
					newOrder.getmyOrderLock().unlock();
				}

			}
		}
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message +"  in if(!lockedOnOrder)  for " + newOrder.toString(),this);

	}

	public void serveOrder(Order toServOrder) throws InterruptedException{
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,">>>>"+message +" for " + toServOrder.toString(),this);
		// Set not locked on order
		lockedOnOrder = false;
		// If not locked on order
		if(!lockedOnOrder){
				try{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST,">>>>"+message +" for " + toServOrder.toString()+" try to get the order locked",this);
					// Tries to lock the order
					lockedOnOrder = toServOrder.getmyOrderLock().tryLock();

			}finally{
				// If Failed to lock the order
				if(!lockedOnOrder)
				{
					synchronized (this) {
						// Writes to log
						waiterLogger.logger().log(Level.FINEST,">>>>"+message +" for " + toServOrder.toString()+" failed to get the order locked going to wait(200)",this);
						// wait
						wait(200);
					}

				}
				// If locked the order
				else{
					// Writes to log
					waiterLogger.logger().log(Level.FINEST,">>>>"+message +" for " + toServOrder.toString()+ " locked on order",this);
					// Set the order status to customer can eat
					toServOrder.setStatus(status.CustomerCanEat);
					// SDEt not locked on order
					this.lockedOnOrder = false;
					// Remove the order from the waiters to do list
					allMyToDo.remove(toServOrder);
					// Unlocks the order
					toServOrder.getmyOrderLock().unlock();
				}

			}
		}
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message +" for " + toServOrder.toString(),this);
	}
	@Override
	public void OrderWaiterListenerStatus(Order checkOrderStatus) {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		waiterLogger.logger().log(Level.FINEST,">>>>"+message +" for " + checkOrderStatus.toString(),this);
		// Adds the order to the waiters to do list
		allMyToDo.add(checkOrderStatus);
		synchronized (this) {
			// Notify the waiter
			this.notify();
			// Writes to log
			waiterLogger.logger().log(Level.FINEST,">>>>"+message +"got notified for " + checkOrderStatus.toString(),this);
		}
		// Writes to log
		waiterLogger.logger().log(Level.FINEST,"<<<<"+message +" for " + checkOrderStatus.toString(),this);
	}
	public double getWaiterSumOfBills(){
		int wSumOfBills = 0;
		// Goes through all the closed orders
		for (int i = 0 ; i < myServedAndPayed.size() ; i ++ ){
			// Sums the orders bill
			wSumOfBills +=myServedAndPayed.elementAt(i).getOrderBill();
		}
		return wSumOfBills;

	}
	


}
