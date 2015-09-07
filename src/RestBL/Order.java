package RestBL;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;
//import sun.misc.Lock;
import java.util.logging.Level;


public class Order {
	public enum status  {ResivedTable,FoodReady,WaitingFood,WaitingMenu,CustomerCanEat,WaitForOrder,New,WaitingForBill,Closed,BrowsingMenu}
	
	private int numOfOrder;
	private String message;
	private Table table;
	private int orderBill;
	private boolean isReady;
	private status orderStatus;
	private Waiter theWaiter;
	private Kitchen kitchen;
	private Customer theCustomer;
	private boolean notifiedWaiter = false;
	private boolean notifiedCustomer = false;

	private Vector<WaiterOrderListener> allWaiterOrderL;
	private Vector<CustomerOrderListeners> allCustomerOrderL;
	private WorkingDay theLogger;

	private ReentrantLock myOrderLock = new ReentrantLock();


	

	public Order(int numOfOrder, Table table,Waiter theWaiter,Kitchen kitchen,Customer theCustomer) {
		theLogger = new WorkingDay("orderLogger",this);
		theLogger.setLoggerLevel(Level.FINEST);
		this.numOfOrder = numOfOrder;
		this.table = table;
		this.theCustomer=theCustomer;
		this.theWaiter = theWaiter;
		this.kitchen = kitchen;
		setOrderBill();
		isReady=false;
		
		
		registerWaiterOrderListeners(theWaiter);
		registerCustomerOrderListeners(theCustomer);
		orderStatus = status.ResivedTable;
		
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	message + " created" ,this);
	}
	@Override
	public String toString() {
		return "Order [orderNum=" + numOfOrder + ", orderStatus=" + orderStatus
				+ ", OrderTable=" + table + ", myCustomer=" + theCustomer.getCustName()
				+ "]";
	}
	public void setStatus(status myNewStatus){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	">>>>"+message + "old status, "+this.getOrderStatus().toString()+" for new status, "+myNewStatus.toString(),this);
		// Gets the new order status
		orderStatus = myNewStatus;
		// Checks if the new status is related to the customers
		if((orderStatus == status.BrowsingMenu ) || (orderStatus == status.CustomerCanEat) 
				||(orderStatus ==status.Closed)){
			// Change boolean to false - waiters not notified
			notifiedWaiter = false;
			// Notify customers
			notifyAllCustomerOrderListeners();
			// Change boolean to true - customers were notified
			notifiedCustomer = true;
		}
		else{
			// Change boolean to false - customers not notified
			notifiedCustomer = false;
			// Notify waiters
			notifyAllWaiterOrderListeners();
			// Change boolean to true - waiters were notified
			notifiedWaiter = true;
		}
		// Writes to log
		theLogger.logger().log(Level.FINEST,	"<<<<"+message + "old status, "+this.getOrderStatus().toString()+" for new status, "+myNewStatus.toString(),this);

	}
	public Table getOrderTable(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	"<<<<"+message +" return," + table.toString(),this);

		return this.table;
	}
	public status getOrderStatus(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	"<<<<"+message +" return,"+orderStatus.toString(),this);

		return orderStatus;

	}
	public int getNumOfOrder() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	"<<<<"+message +" return,"+numOfOrder,this);

		return numOfOrder;
	}
	public void setOrderNum(int numOfOrder) {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	">>>>"+message +"set order num,"+numOfOrder,this);
		
		this.numOfOrder = numOfOrder;
	}
	public double getOrderBill() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	"<<<<"+message +" return,"+orderBill,this);
		// If the order is closed
		if(orderStatus == status.Closed){
			// Close the order handler
			theLogger.theFileHandler().close();
		}
		return orderBill;
	}
	public void setOrderBill() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	">>>>"+message,this);
		// Calculate the order bill via Math.Random
		this.orderBill = (int)(Math.random()*100+100);
	}
	public void orderIsReady(){
		this.isReady = true;
	}
	public void registerWaiterOrderListeners(WaiterOrderListener newListener){		
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +"new Listener,"+newListener.toString();
		theLogger.logger().log(Level.FINEST,	">>>>"+message,this);
		// Adds the new listener to the order status
		this.allWaiterOrderL.add(newListener);
		// Writes to log
		theLogger.logger().log(Level.FINEST,	"<<<<"+message,this);
	}
	public synchronized void notifyAllWaiterOrderListeners(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +"have new Status to notify all listeners for Order " +this.getNumOfOrder()+" Status is: "+this.getOrderStatus() ;
		theLogger.logger().log(Level.FINEST,	">>>>"+message,this);
		// Going through all the Waiters orders listeners
		for(int i=0 ; i <this.allWaiterOrderL.size();i++){
			// Notify waiters
			this.allWaiterOrderL.elementAt(i).OrderWaiterListenerStatus(this);
			// Writes to log
			theLogger.logger().log(Level.FINEST,	">>>>"+message+ "to, "+this.allWaiterOrderL.elementAt(i).toString() ,this);
		}
		// Writes to log
		theLogger.logger().log(Level.FINEST,	"<<<<"+message,this);	
	}
	public void registerCustomerOrderListeners(CustomerOrderListeners newListener){	
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +"new Listener,"+newListener.toString();
		theLogger.logger().log(Level.FINEST,	">>>>"+message,this);
		// Adds the new listener to the order status
		this.allCustomerOrderL.add(newListener);
		// Writes to log
		theLogger.logger().log(Level.FINEST,	"<<<<"+message,this);		
	}
	public synchronized void notifyAllCustomerOrderListeners(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() +"have new Status to notify all listeners for Order " +this.getNumOfOrder()+" Status is: "+this.getOrderStatus() ;
		theLogger.logger().log(Level.FINEST,	">>>>"+message,this);
		// Going through all the customer orders listeners
		for(int i=0 ; i <this.allCustomerOrderL.size();i++){
			// Notify customers
			this.allCustomerOrderL.elementAt(i).OrderCustomerListenerStatus(this);
			// Writes to log
			theLogger.logger().log(Level.FINEST,	">>>>"+message+ "to, "+this.allCustomerOrderL.elementAt(i).toString() ,this);
		}
		// Writes to log
		theLogger.logger().log(Level.FINEST,	"<<<<"+message,this);			
	}
	public void setOrderWaiter(Waiter aWaiter){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString() + " for, "+aWaiter.toString() ;
		theLogger.logger().log(Level.FINEST,	">>>>"+message,this);
		// Gets the sent waiter
		this.theWaiter = aWaiter;
		// Register the waiter to order status listeners
		registerWaiterOrderListeners(aWaiter);
		// If the waiter hasn't been notified
		if(!notifiedWaiter){
			// Notify all waiters 
			notifyAllWaiterOrderListeners();
		}
		
		// Writes to log
		theLogger.logger().log(Level.FINEST,	"<<<<"+message,this);

	}
	public ReentrantLock getmyOrderLock(){
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	"<<<<"+message +" return,"+myOrderLock,this);
		
		return this.myOrderLock;
	}
	public Customer getCustomer() {
		// Writes to log
		message = "["+Thread.currentThread().getStackTrace()[1].getMethodName()+"] "+ this.toString();
		theLogger.logger().log(Level.FINEST,	"<<<<"+message +" return,"+theCustomer.toString(),this);
		
		return theCustomer;
	}

}
