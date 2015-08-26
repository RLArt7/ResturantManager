
public class Order {
	private int numOfOrder;
	private Table table;
	private int price;
	private boolean isReady;

	

	public Order(int numOfOrder, Table table, int price) {
		this.numOfOrder = numOfOrder;
		this.table = table;
		this.price = price;
		isReady=false;
	}

	public int getNumOfOrder() {
		return numOfOrder;
	}
	
	public void orderIsReady(){
		this.isReady = true;
	}

}
