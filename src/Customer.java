
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
	
	
}
