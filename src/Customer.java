import java.util.Calendar;

public class Customer extends Person{
	
	private String whileWaiting;
		
	public Customer(String name,String whileWaiting) {
		super(name);
		this.whileWaiting = whileWaiting;
	}
	public Customer(String whileWaiting,int counter) {
		super("customer_" + ++counter);
		this.whileWaiting = whileWaiting;
	}
	

	@Override
	public String toString() {
		return "Customer: "+this.getName() + "  whileWaiting: " + whileWaiting;
	}
	
	@WhileWaiting
	public void readNewsPaper() throws InterruptedException {
		synchronized (this) {
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Customer #" + this.getName() + " is reading a paper while waiting for his meal");

//			theAirport.addWaitingAirplane(this);

			wait();
		}
	}
	@WhileWaiting
	public void playBubbles() throws InterruptedException {
		synchronized (this) {
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Customer #" + this.getName() + " is play Bubbles while waiting for his meal");

//			theAirport.addWaitingAirplane(this);

			wait();
		}
	}
	@WhileWaiting
	public void doHomework() throws InterruptedException {
		synchronized (this) {
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Customer #" + this.getName() + " is doing Homework while waiting for his meal");

//			theAirport.addWaitingAirplane(this);

			wait();
		}
	}
	@NotAllowed
	public void talkOnThePhone() throws InterruptedException {
		synchronized (this) {
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Customer #" + this.getName() + " is talknig on the phone while waiting for his meal");

//			theAirport.addWaitingAirplane(this);

			wait();
		}
	}
	@NotAllowed
	public void whatchMovie() throws InterruptedException {
		synchronized (this) {
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Customer #" + this.getName() + " is watching a movie while waiting for his meal");

//			theAirport.addWaitingAirplane(this);

			wait();
		}
	}
	public void waitingForMenu() throws InterruptedException {
		synchronized (this) {
			System.out.println(Calendar.getInstance().getTimeInMillis()
					+ " Customer #" + this.getName() + " is waiting tothe menu");
//			theAirport.addWaitingAirplane(this);
			wait();
		}
	}
	public void eat() throws InterruptedException {
		long actionTime = (long) (Math.random() * 10000);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " start to eat for" + actionTime
				+ "ms");
		Thread.sleep(actionTime);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " finished watching");
	}
	
	
}
