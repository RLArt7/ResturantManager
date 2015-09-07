package RestBL;
import java.util.Calendar;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.logging.*;
//import sun.misc.Lock;

import RestBL.Order.status;



public class Customer extends Thread implements CustomerOrderListeners,RestaurantListener{

	private String whileWaiting;
	private String name;
	private Resturant resturant;
	private boolean canRegister= true;
	private boolean isFinished = false;
	private WorkingDay theCustLogger;

	private Order myOrder ;
	private boolean isRegisterder = false;
	private Lock myCustomerlock;
	private boolean lockedOnRest = false;
	private boolean madeHisOrder = false;
	private boolean isWaitingforFood =false;
	private String massage;
	private CountDownLatch canEatLatch = new CountDownLatch(1);
	private CountDownLatch closedLatch = new CountDownLatch(1);
	private CountDownLatch browsingMenuLatch = new CountDownLatch(1);

	public Customer(String name,String whileWaiting,Resturant resturant) {
		this.name = name;
		this.whileWaiting = whileWaiting;
		this.resturant=resturant;

		theCustLogger = new WorkingDay(name,this);
		theCustLogger.setLoggerLevel(Level.FINEST);
//		theCustLogger.logger().setLevel(Level.FINEST);
	}
	public Customer(String whileWaiting,int counter,Resturant resturant) {
		this.name = "customer_" + (counter+1);
		this.whileWaiting = whileWaiting;
		this.resturant = resturant;
		theCustLogger = new WorkingDay(this.name,this);
		
		theCustLogger.logger().setLevel(Level.FINEST);

	}


	@Override
	public String toString() {
		return "Customer: "+this.getName() + "  whileWaiting: " + whileWaiting;
	}
	public String getCustName(){
		return name;
	}
	public Order getOrder(){
		return myOrder;
	}
	public void setOrder(Order myNewOrder){
		this.myOrder = myNewOrder;
	}
	public void  WhileWaiting(){ 
		System.out.println("While Waiting Method");

		// Writing into the log that WhileWaiting method started
		theCustLogger.logger().log(Level.FINEST, "WhileWaiting method started started and locked",this);

		// Locking the lock
		this.myCustomerlock.lock();

		// Getting all the methods in customer class
		Method[] myWaitMethods = this.getClass().getMethods();

		// Running over every Method
		for(Method waitMethod : myWaitMethods){
			// Checks if the method have "WhileWait" annotation
			if(waitMethod.isAnnotationPresent(WhileWaiting.class)){
				// Writes to log
				theCustLogger.logger().log(Level.FINEST, "WhileWaiting if-isAnnotationPresent()"+" looking for actions: " +waitMethod.getName() + " action is : " + this.whileWaiting,this);
				// Checks what action this customer does
				if(waitMethod.getName().equals(this.whileWaiting)){
					// Writes to log 
					theCustLogger.logger().log(Level.FINEST, "WhileWaiting if-func found is equals()",this);
					try {
						// Writes to log
						theCustLogger.logger().log(Level.FINEST, "WhileWaiting try to make action",this);

						// Starts the relevant method
						waitMethod.invoke(this);

						// Writes to log
						theCustLogger.logger().log(Level.FINEST, "WhileWaiting sucessfully made action",this);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						//Writing error to log
						theCustLogger.logger().log(Level.FINEST, e.getMessage(),this);
					}

					//Writing to log
					theCustLogger.logger().log(Level.FINEST, "WhileWaiting method Finished",this);

					// Stopping the for, Method found
					break;

				}
			}
		}
		// Unlocking the lock
		this.myCustomerlock.unlock();
	}

	@WhileWaiting
	public void readNewsPaper() {
		//Writing to log
		theCustLogger.logger().log(Level.FINEST,"Customer " + this.getCustName()+ " is reading the newspaper.",this);
		theCustLogger.logger().log(Level.INFO,"Customer " + this.getCustName()+ " is reading the newspaper.",this);
	}
	@WhileWaiting
	public void playBubbles() {
		//Writing to log
		theCustLogger.logger().log(Level.FINEST, "Customer " + this.getCustName() + " is playing Bubbles.",this);
		theCustLogger.logger().log(Level.INFO, "Customer " + this.getCustName() + " is playing Bubbles.",this);
	}
	@WhileWaiting
	public void doHomework(){
		//Writing to log
		theCustLogger.logger().log(Level.FINEST, "Customer " + this.getCustName() + " is doing homework.",this);
		theCustLogger.logger().log(Level.INFO, "Customer " + this.getCustName() + " is doing homework.",this);
	}
	@NotAllowed
	public void talkOnThePhone(){
		//Writing to log
		theCustLogger.logger().log(Level.FINEST,"Customer " + this.getCustName() + " is talking on the phone.",this);
		theCustLogger.logger().log(Level.INFO,"Customer " + this.getCustName() + " is talking on the phone.",this);
	}
	@NotAllowed
	public void whatchMovie(){
		//Writing to log
		theCustLogger.logger().log(Level.FINEST,"Customer " + this.getCustName() + " is watching a movie.",this);
		theCustLogger.logger().log(Level.INFO,"Customer " + this.getCustName() + " is watching a movie.",this);
	}
	public void waitingForMenu(){
		//Writing to log
		theCustLogger.logger().log(Level.FINEST, "Customer " + this.getCustName() + " is waiting for the menu.",this);
		theCustLogger.logger().log(Level.INFO, "Customer " + this.getCustName() + " is waiting for the menu.",this);

		try {
			// Threads sleeps to mimic the time it takes to browse the menu
			Thread.sleep((long) (Math.random()*2000+1000));
		} catch (InterruptedException e) {
			//Writing the exception to log
			theCustLogger.logger().log(Level.FINEST, e.getMessage(),this);
		}
	}
	public void browseMenu(){
		//Writing to log
		System.out.println("browesing Menu Method");
		theCustLogger.logger().log(Level.FINEST, "Customer " + this.getCustName() + " is browsing in the menu.",this);
		theCustLogger.logger().log(Level.INFO, "Customer " + this.getCustName() + " is browsing in the menu.",this);

		try {
			// Threads sleeps to mimic the time it takes to browse the menu
			Thread.sleep((long) (Math.random()*2000+1000));
		} catch (InterruptedException e) {
			//Writing the exception to log
			theCustLogger.logger().log(Level.FINEST, e.getMessage(),this);
		}


	}
	public void makeOrder(){
		//Writing to log
		System.out.println("making Order Method");

		theCustLogger.logger().log(Level.FINEST,"Customer " + this.getCustName()+ " is making his order.",this);
		theCustLogger.logger().log(Level.INFO,"Customer " + this.getCustName()+ " is making his order.",this);

		// Change the order status to WaitForOrder
		myOrder.setStatus(status.WaitForOrder);

	}
	public void waitingForBill(){
		//Writing to log
		theCustLogger.logger().log(Level.FINEST, "Customer " + this.getCustName() + " ask for Bill",this);
		theCustLogger.logger().log(Level.INFO, "Customer " + this.getCustName() + " ask for Bill",this);

		// Change the order status to waitForBill
		this.myOrder.setStatus(status.WaitingForBill);
	}
	public void eat(){
		//Writing to log
		theCustLogger.logger().log(Level.FINEST, "Customer " + this.getCustName() + " is eating.",this);
		theCustLogger.logger().log(Level.INFO, "Customer " + this.getCustName() + " is eating.",this);

		try {
			// Threads sleeps to mimic the time it takes to eat
			Thread.sleep((long)Math.random()*2000+1000);
		} catch (InterruptedException e) {
			//Writing the exception to log
			theCustLogger.logger().log(Level.FINEST, e.getMessage(),this);
		}
	}
	@Override
	public void run() {
		// Writes to log
		theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name,this );
		//while the restaurant is opened and the Customer yet to be finished try to register to line
//		System.out.println(resturant.isRunning() + " ; " + isFinished + " : " + canRegister);
		while((resturant.isRunning() && (!isFinished) && (canRegister))){
			// register to restaurant listener
			resturant.registerToRestaurantListener(this);

			// Writes to log
			theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name+" in while((theRest.isRunning() && (!this.isFinished)))",this );

			// If the rest is not open for customers
			if(!resturant.isOpenForCustomers()){ 
				synchronized(this){
					try {
						// Wait for the rest to be open for customers
						wait();
						// Writes to log that the wait is finished
						theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name+" in while((theRest.isRunning() && (!this.isFinished))) sucessfully finished wait",this );

					} catch (InterruptedException e1) {
						// Write exception to log
						theCustLogger.logger().log(Level.FINEST, e1.getMessage(),this );
					}
				}

			}
			// While the rest is open for customers and the customer hasn't registered yet
			while(((resturant.isOpenForCustomers()) && (!isRegisterder))){
				// Writes to log
				theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
						+" in while(((theRest.isOpenForCustomers()) && (!this.isRegisterder)))",this );
				try {
					// Writes to log
					theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
							+" in while(((theRest.isOpenForCustomers()) && (!this.isRegisterder)))\n"+"theRest.getMyRestLock().tryLock(); ",this );
					// Try to lock the rest lock
					lockedOnRest = resturant.getMyRestLock().tryLock();

				}finally {
					// If failed to lock the rest lock
					if (!(lockedOnRest)) {
						// Writes to log
						theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
								+" in while(((theRest.isOpenForCustomers()) && (!this.isRegisterder)))\n"+"theRest.getMyRestLock().tryLock(); failed, try to wait ",this );

						try {
							// Writes to log
							theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
									+" in while(((theRest.isOpenForCustomers()) && (!this.isRegisterder)))\n"+"theRest.getMyRestLock().tryLock(); failed, sucessfully wait ",this );
							synchronized(this){
								// Wait 1 second and try again 
								this.wait(1000);
							}
						} catch (InterruptedException e) {
							// Write exception to log
							theCustLogger.logger().log(Level.FINEST, e.getMessage(),this );
						}
					}
					// If locked the rest lock
					else if(lockedOnRest){
						// Writes to log
						theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
								+" in while(((theRest.isOpenForCustomers()) && (!this.isRegisterder)))\n"+"theRest.getMyRestLock().tryLock(); sucess ",this );

						try {
							// Register to the 'customers waiting in line' vector
							resturant.regsterCustomerToLine(this);


						} catch (Exception e) {
							// Write exception to log
							theCustLogger.logger().log(Level.FINEST, e.getMessage(),this );
						}
						// Unlocks the rest lock
						resturant.getMyRestLock().unlock();
					}
				}
				// If the customer can register
				if(canRegister){
					// Writes to log
					theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name+" in while((theRest.isRunning() && (!this.isFinished)))\n"
							+"if(canRegister)---->"+canRegister,this );
				}
				else{
					// Writes to log
					theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name+" in while((theRest.isRunning() && (!this.isFinished)))\n"
							+"if(canRegister)---->"+canRegister + "break!",this );
					break;
				}

			}
			// If the customer has registered and not places his order
			while(isRegisterder && (!this.madeHisOrder)){
				// Writes to log
				theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
						+" in while(this.isRegisterder)"+" and trying to wait",this );
				//need to wait until he will get his menu
				try{

					synchronized(this){
						// Writes to log
						theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
								+" in while(this.isRegisterder)"+" sucessfully in wait",this );
						// Wait for browsingMenuLatch
						browsingMenuLatch.await();

						// Browse the menu
						browseMenu();
						// Place his order
						makeOrder();
						// Change boolean to true
						this.madeHisOrder = true;
					}
				}catch(Exception e){
					// Write exception to log
					theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" run() started for Customer: "+ this.name
							+" in while(this.isRegisterder)"+" failed in wait\n"+e.getMessage(),this );
					theCustLogger.logger().log(Level.FINEST, e.getMessage(),this );

				}
				//Customer is doing action while he waits for food
				WhileWaiting();
			}
			// While the customer has made his order and not waiting for food
			while(!isWaitingforFood && (madeHisOrder)){
				// Writes to log
				massage = (" run() started for Customer: "+ this.name+" in while(!isWaitingforFood)"+" and trying to wait");
				theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+massage
						+" in while(!this.madeHisOrder)"+" and trying to wait",this );

				try {
					synchronized (this) {
						// Writes to log
						massage = (" run() started for Customer: "+ this.name+" in while (!isWaitingforFood)"+"  go in wait()");
						theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+massage,this );

						// Waits to canEatLatch (Waits to the food)
						canEatLatch.await();

						// Set isWaitingforFood to true
						isWaitingforFood = true;

						// The customer eats
						eat();
						// The customer asks for the bill
						waitingForBill();

					}
				}catch(Exception e) {
					// Write exception to log
					massage = (" run() started for Customer: "+ this.name+" in  while(!isWaitingforFood)"+" failed to wait()");
					theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+massage
							+" in while(!this.madeHisOrder)"+" and trying to wait",this );
					theCustLogger.logger().log(Level.FINEST, e.getMessage(),this );

				}


			}
			// While the customer is registered to the rest and has not finished it's run
			while(!isFinished && isRegisterder){

				try {
					// Write exception to log
					massage = (" run() started for Customer: "+ this.name+" in  while(!isFinished)"+" sucessfully in wait after askForBill(); ");
					theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+massage
							+" in while(!this.madeHisOrder)"+" and trying to wait",this );

					synchronized (this) {
						// Waits for closedLatch
						closedLatch.await();
					}

				} catch (InterruptedException e) {
					// Write exception to log
					massage = (" run() started for Customer: "+ this.name+" in  while(!isFinished)"+" failed in wait after askForBill(); "+ e.getMessage());
					theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+massage,this );
					theCustLogger.logger().log(Level.FINEST, e.getMessage(),this );
				}

			}

			// If the customer has registered to the rest line
			if(isRegisterder){
				// Write to log
				massage = (" run() finished for Customer: "+ this.name + " the Customer Payed:" + myOrder.getOrderBill());
				theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+massage,this );

			}
			// If the customer couldn't register to the rest line
			else{
				// Write to log
				massage = (" run() finished for Customer: "+ this.name + " restaurant was closed!!!!!!!!");
				theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+massage,this );
			}
			// Write to log
			theCustLogger.logger().log(Level.FINEST,this.getCustName() + " sucessfully finished",this );
			// Close the log handler
			theCustLogger.theFileHandler().close();
		}
	}
	public Lock getmyCustLock(){
		return myCustomerlock;

	}
	public void setIsRegisterder(boolean Register){
		this.isRegisterder = Register;
	}
	public void setCanRegister(){
		this.canRegister = false;
	}
	@Override
	public void restStatus(boolean status) {
		// Customer is notifying itself about a change in the restaurant status
		synchronized (this) {
			this.notify();
			theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" restStatus(boolean status) Customer:"+this.name+" notified himself -->" +status);

		}
	}
	@Override
	public void OrderCustomerListenerStatus(Order checkOrderStatus) {
		// Write to log
		theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" OrderCustomerListenerStatus() Customer:"+this.name+"Start for order Status: "
				+checkOrderStatus.getNumOfOrder()+" and in status: " +checkOrderStatus.getOrderStatus().toString() ,this);

		// Checks the order status
		if(checkOrderStatus.getOrderStatus() == status.BrowsingMenu) {
			// Letting the customer know it can browse the menu
			this.browsingMenuLatch.countDown();
			// Write to log
			theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" OrderCustomerListenerStatus() Customer:" + this.getCustName() + " has  sucessfully notified himself -->  "
					+checkOrderStatus.getOrderStatus().toString(),this);	
		}
		// Checks the order status
		else if(checkOrderStatus.getOrderStatus() == status.CustomerCanEat) {
			//
			if(!isWaitingforFood){
				// Letting the customer know it can eat
				this.canEatLatch.countDown();
				// Write to log
				theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" OrderCustomerListenerStatus() Customer:" + this.getCustName() + " has  sucessfully notified himself --> "
						+checkOrderStatus.getOrderStatus().toString(),this);
			}

			else{
				// Write to log
				theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" OrderCustomerListenerStatus() Customer:" + this.getCustName() + " failed to notified himself --> "
						+checkOrderStatus.getOrderStatus().toString() +" couse by:" + isWaitingforFood,this);
			}
		}
		// Checks the order status
		else if(checkOrderStatus.getOrderStatus() == status.Closed) {
			// Updates that the customer has finished it's run
			isFinished = true;
			this.closedLatch.countDown();

		}
		// Write to log
		theCustLogger.logger().log(Level.FINEST, this.getClass().getName()+" OrderCustomerListenerStatus() Customer:"+this.name+"finished for order Status: "
				+checkOrderStatus.getNumOfOrder(),this);

	}


}
