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
		long actionTime = (long) (Math.random() * 10000);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " start reading a paper for" + actionTime
				+ "ms");
		Thread.sleep(actionTime);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " finished reading");
	}
	@WhileWaiting
	public void playBubbles() throws InterruptedException {
		long actionTime = (long) (Math.random() * 10000);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " start play Bubbles for" + actionTime
				+ "ms");
		Thread.sleep(actionTime);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " finished playing");
	}
	@WhileWaiting
	public void doHomework() throws InterruptedException {
		long actionTime = (long) (Math.random() * 10000);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " start doing Homework for" + actionTime
				+ "ms");
		Thread.sleep(actionTime);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " finished Homework");
	}
	@NotAllowed
	public void talkOnThePhone() throws InterruptedException {
		long actionTime = (long) (Math.random() * 10000);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " start talknig on the phone for" + actionTime
				+ "ms");
		Thread.sleep(actionTime);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " finished talking");
	}
	@NotAllowed
	public void whatchMovie() throws InterruptedException {
		long actionTime = (long) (Math.random() * 10000);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " start watching a movie for" + actionTime
				+ "ms");
		Thread.sleep(actionTime);
		System.out.println(Calendar.getInstance().getTimeInMillis()
				+ " Customer #" + this.getName() + " finished watching");
	}
	
	
	
}
