package RestBL;
import java.io.File;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Program {
	private static Vector<Customer> allCustomers;
	private static WorkingDay mainLogger= new WorkingDay("ProgramLogger",new Object());
	private static Resturant resturant;
	public static void main(String[] args) {
		mainLogger.setLoggerLevel(Level.INFO);
		allCustomers = new Vector<Customer>();
		resturant = initializeResturantFromXml(); 


		//need to pass to it the relevant data
		if(resturant == null){
			mainLogger.logger().log(Level.SEVERE, "[Error] : The XML File was Empty or Corrupted!");
			return;
		}
		for(Customer cast:resturant.getCustomers()){
			System.out.println(cast.toString());
		}

		resturant.setRunning(true);
		Scanner input =new Scanner(System.in);
		resturant.Waiterstarter(); //starts all waiters assigned to this restaurant 
		customerStarter(allCustomers);//starts all customers 
		boolean brakeloop=true;
		while (brakeloop){
			System.out.println("Press 1 to add Customer:\n"
					+ "Press 2 to add Waiter:\n"
					+ "Press 3 to show waiting customers:\n"
					+ "Press 4 to show tabels Data:\n"
					+ "Press 5 to show resturant profits until now:\n"
					+ "Press 6  to exit and close the day:");
			String choise=input.nextLine();
			switch (choise){
			case "1":
				mainLogger.logger().log(Level.INFO, "User Choose to Add Customer");
				resturant = addCustomer(resturant,input);

				//				for(Customer cast:resturant.getCustomers()){
				//					System.out.println(cast.toString());
				//				}
				break;
			case "2":
				mainLogger.logger().log(Level.INFO, "User Choose to Add Waiter");
				resturant = addWaiter(resturant,input);
				break;
			case "3":
				mainLogger.logger().log(Level.INFO, "Showing Waiting Customer List");
				showWaitingCustomers(resturant);
				break;
			case "4":
				mainLogger.logger().log(Level.INFO, "Showing Tabels Data");
				showTablesData(resturant);
				break;
			case "5":
				mainLogger.logger().log(Level.INFO, "Showing Resturant Profits");
				System.out.println("today profit is: "+resturant.getSumOfBills());
				break;
			case "6":
				mainLogger.logger().log(Level.INFO, "Start Closing The Day prosses");
				resturant.setRunning(false);
				resturant.forcedTimeToClose();
				resturant.closeTheDay();
				brakeloop=false;
				System.out.println("GOOD BYE...");
				mainLogger.theFileHandler().close();
				break;
			default:
				System.out.println("You Enter Invalid key please try again...");
				continue;
			}
		}

	}


	private static void showTablesData(Resturant resturant) {
		// Running over all of the waiters
		for(int i = 0; i < resturant.getWaitersThatFinishedShift().size();i++){
			// Running over every order
			for(int j = 0; j < resturant.getWaitersThatFinishedShift().get(i).getMyOrders().size(); j++){
				// Get the table
				Table tbl = resturant.getWaitersThatFinishedShift().get(i).getMyOrders().get(j).getOrderTable();

				// Get the customer
				Customer cust =  resturant.getWaitersThatFinishedShift().get(i).getMyOrders().get(j).getCustomer();

				// Prints the data
				System.out.println("Table no. " + tbl.getNumOfTblID() + " : " +
						" has customer " + cust.getCustName() +
						" and the waiter is " + resturant.getWaitersThatFinishedShift().get(i).getWaiterName());
			}
		}
	}

	private static void showWaitingCustomers(Resturant resturant) {
		for(Customer customer:resturant.getCustomers()){
			System.out.println(customer.toString());
		}
	}

	private static Resturant addWaiter(Resturant resturant, Scanner input) {
		mainLogger.logger().log(Level.INFO, "Waiter Added Succssesfuly..");
		System.out.println("Waiter Added Succssesfuly..");
		resturant.addWaiter(new Waiter(resturant));


		return resturant;
	}

	private static Resturant addCustomer(Resturant resturant, Scanner input) {

		System.out.println("Please Enter the Customer Name: (optional) \n");
		String name = input.nextLine();
		System.out.println("Please Enter the customer whileWaiting:");
		System.out.println("Customer's actions Options: ");
		System.out.println("1) read the newspaper");
		System.out.println("2) play Bubbles ");
		System.out.println("3) talk on the phone");
		System.out.println("4) do homework");
		System.out.println("5) watch a movie ");
		// Choosing the customer's action
		int actionChoice = input.nextInt();
		String whileWaiting= "";
		switch(actionChoice){
		case 1:
			whileWaiting = "readNewsPaper";
			break;
		case 2:
			whileWaiting = "playBubbles";
			break;
		case 3:
			whileWaiting = "talkOnThePhone";
			break;
		case 4:
			whileWaiting = "doHomework";
			break;
		case 5:
			whileWaiting = "watchMovie";
			break;
		}
		input.nextLine();
		Customer temp;
		if(name.equals("")){
			temp = new Customer(whileWaiting,resturant.getCustomers().size(),resturant);
		}else{
			temp = new Customer(name,whileWaiting,resturant);
		}
		resturant.addCustomer(temp);
		return resturant;

	}

	private static Resturant initializeResturantFromXml() {

		try {	
			File inputFile = new File("configurationFile.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			String message = "Root element : " + doc.getDocumentElement().getNodeName();
			////Resturant
			mainLogger.logger().log(Level.INFO, message);
			message = doc.getDocumentElement().getNodeName() + " name :" + doc.getDocumentElement().getAttribute("name") ;
			mainLogger.logger().log(Level.INFO,message);
			String tempName =doc.getDocumentElement().getAttribute("name");
			int numOfCustPrDay =Integer.parseInt(doc.getDocumentElement().getAttribute("maxCustomersPerDay"));
			int numofSts = Integer.parseInt(doc.getDocumentElement().getAttribute("numOfSeats"));
			Resturant returnRest;
			int maxWaiters = 0;
			int maxCustomersPerWaiter = 0;
			Node nNode = doc.getElementsByTagName("Waiters").item(0);
			Element eElement;
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				eElement = (Element) nNode;
				maxWaiters = (Integer.parseInt(eElement.getAttribute("maxWaitersInShift")));
				maxCustomersPerWaiter = (Integer.parseInt(eElement.getAttribute("maxCustomersCuncurrentlPerWaiter")));
			}
			returnRest = new  Resturant(tempName,numOfCustPrDay,numofSts,maxWaiters,maxCustomersPerWaiter);
			//			for (int i= 0 ; i<maxWaiters;i++){
			//				Waiter temp = new Waiter(returnRest); 
			//				System.out.println(temp + " Program Create waiter");
			//				returnRest.addWaiter(temp);
			//			}
			//        	 returnRest.setMaxCustomersPerWaiter(maxCustomersPerWaiter);
			NodeList nList = doc.getElementsByTagName("Customer");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					eElement = (Element) nNode;
					Customer tempCustomer = new Customer(eElement.getAttribute("name"),eElement.getAttribute("whileWaiting"),returnRest);
					allCustomers.add(tempCustomer);
				}

			}
			return returnRest;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;

	}
	public static void customerStarter(Vector<Customer> allCustomers){
		// Going through every customer
		for(int i = 0 ; i <allCustomers.size() ; i++){
			// Starts the customer
			allCustomers.elementAt(i).start();
		}
	}


}
