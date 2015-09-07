package RestBL;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;

import RestBL.Order.status;

public class Kitchen {
	private Vector <Order> newOrders;
	private String message;
	private boolean ResturantRunning = true;
	private boolean kitchenRunning;
	private WorkingDay kitchenLogger;
	
	public Kitchen(Resturant theRest) {
		// Setting order vector
		newOrders = new Vector<Order>();
		// Starts the logger
		kitchenLogger= new WorkingDay("kitchen",this);
		// Opens the kitchen
		setOpen(true);
		// Writes to log 
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] ";
		kitchenLogger.logger().log(Level.FINEST,"<<<<"+message+"created",this);
		kitchenLogger.setLoggerLevel(Level.FINEST);
		
		
	}
	public void newOrderUpdate(Order newOrder) {
		// Writes to log 
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] ";
		kitchenLogger.logger().log(Level.FINEST,">>>>"+message+"recived new order to work on: " + newOrder.getNumOfOrder()+" ,add the order in to kitchen Order List",this);
		// Adds the new order
		newOrders.add(newOrder);
		// Start making the orders
		maikingTheOrders();
		// Writes to log 
		kitchenLogger.logger().log(Level.FINEST,"<<<<"+message+"recived new order to work on: " + newOrder.getNumOfOrder()+" ,add the order in to kitchen Order List",this);

	}
	public synchronized void maikingTheOrders(){
		// Writes to log 
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] ";
		kitchenLogger.logger().log(Level.FINEST,">>>>"+message,this);
		// While there are orders to make
		while(!newOrders.isEmpty()){
			// Writes to log 
			kitchenLogger.logger().log(Level.FINEST,message+" have order to do ",this);
		try {
			// Writes to log 
			kitchenLogger.logger().log(Level.FINEST,message+"  Starts the cocking of Order: " +newOrders.elementAt(0).getNumOfOrder() ,this);
			// Mimic the time it takes to make the order
			Thread.sleep((long) (Math.random()*2000+1000));
			// Writes to log 
			kitchenLogger.logger().log(Level.FINEST,message+"  Finished the cocking of Order: " +newOrders.elementAt(0).getNumOfOrder() ,this);
			
		} catch (InterruptedException e) {
			// Writes exception to log 
			kitchenLogger.logger().log(Level.FINEST,message+ " faild to cook the Order: " + newOrders.elementAt(0).getNumOfOrder() + " Failed in: "+e.getMessage() ,this);
		}
		// Get the first order in orders to make
		Order readyOrder = newOrders.elementAt(0);
		// Writes to log 
		kitchenLogger.logger().log(Level.FINEST,message+" moved the ready Order: " +newOrders.elementAt(0).getNumOfOrder() + " to ReadyOrder list" ,this);
		// Remove the order from orders to make (the order is done)
		newOrders.remove(0);
		// Writes to log 
		kitchenLogger.logger().log(Level.FINEST,message+"set the  Order: " +readyOrder.getNumOfOrder() + " old status from:" + readyOrder.getOrderStatus().toString() ,this);
		// Change the order status to foodready
		readyOrder.setStatus(status.FoodReady);
		// Writes to log 
		kitchenLogger.logger().log(Level.FINEST,"<<<<"+message + "for, " +readyOrder.toString(),this);
		}
		

	}
	public void setOpen(boolean status){
		// Writes to log 
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] ";
		kitchenLogger.logger().log(Level.FINEST,">>>>"+message,this);
		// Change the kitchen status
		kitchenRunning = status;
		// if the kitchen is not running
		if(!status){
			// Close the log handler
			kitchenLogger.theFileHandler().close();
		}
		// Writes to log 
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] ";
		kitchenLogger.logger().log(Level.FINEST,"<<<<"+message,this);
	}
	
}
