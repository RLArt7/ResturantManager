import java.util.Calendar;
import java.util.LinkedList;
import java.util.Queue;

public class Kitchen {
	private Queue<Order> waitingOrders = new  LinkedList<Order>(); 
	
	public void waitingForBill() throws InterruptedException {
		synchronized (this) {
			Order tempOrder = this.waitingOrders.poll();
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Order #" + tempOrder.getNumOfOrder() + " is ready to serve");
//			theAirport.addWaitingAirplane(this);
			wait();
		}
	}
	public void addOrder(Order order){
		this.waitingOrders.add(order);
	}
}
