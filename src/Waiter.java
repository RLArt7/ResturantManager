import java.util.Calendar;

public class Waiter extends Person{
	private static int id_counter = 0;
	private int currentNumOfCustomer;
	
	
	public Waiter(String name) {
		super("Waiter_"+id_counter++);
		currentNumOfCustomer=0;
	}
	public void addCustomer(){
		if(currentNumOfCustomer<2){
			currentNumOfCustomer++;	
		}
	}
	
	//// i think it better to have this in the customer methos "waitingForMenu"
//	public void bringMenu() throws InterruptedException {
//		long actionTime = (long) (Math.random() * 10000);
//		System.out.println(Calendar.getInstance().getTimeInMillis()
//				+ " Waiter #" + this.getName() + " is bringing menu to customer");
//		Thread.sleep(actionTime);
//		System.out.println(Calendar.getInstance().getTimeInMillis()
//				+ " Waiter #" + this.getName() + " deliver the menu");
//	}
	public void takeOrder() throws InterruptedException {
		synchronized (this) {
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Waiter #" + this.getName() + " is taking order now");
//			theAirport.addWaitingAirplane(this);
			wait();
		}
	}
	public void passOrderToKitchen() throws InterruptedException {
		long actionTime = (long) (Math.random() * 10000);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Waiter #" + this.getName() + " is bringing Order to Kitchen");
		Thread.sleep(actionTime);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Waiter #" + this.getName() + " deliver the Order");
	}
	

}
