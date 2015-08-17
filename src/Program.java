import java.io.File;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Program {

	public static void main(String[] args) {
		
		Resturant resturant = initializeResturantFromXml();
		if(resturant == null){
			System.out.println("We have Null here need to throw exception!");
			return;
		}
//		for(Customer cast:resturant.customers){
//			System.out.println(cast.toString());
//		}
		Scanner input =new Scanner(System.in);
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
				resturant = addCustomer(resturant,input);
				
				for(Customer cast:resturant.customers){
					System.out.println(cast.toString());
				}
				break;
			case "2":
				resturant = addWaiter(resturant,input);
//				resturant = addWaiter(resturant);
				break;
			case "3":
//				showWaitingCustomers(resturant);
				break;
//			case "4":
//				showTablesData(resturant);
//				break;
//			case "5":
//				showResturantProfits(resturant);
//				break;
			case "6":
				resturant.closeTheDay();
				brakeloop=false;
				System.out.println("GOOD BYE...");
				break;
			default:
				System.out.println("You Enter Invalid key please try again...");
				continue;
			}
		}

	}

	private static Resturant addWaiter(Resturant resturant, Scanner input) {
		
		return resturant;
	}

	private static Resturant addCustomer(Resturant resturant, Scanner input) {
		System.out.println("Please Enter the Customer Name: (optional)");
		String name = input.nextLine();
		System.out.println("Please Enter the customer whileWaiting:");
		String whileWaiting = input.nextLine();
		Customer temp;
		if(name.equals("")){
			 temp = new Customer(whileWaiting,resturant.customers.size());
		}else{
			 temp = new Customer(name,whileWaiting);
		}
		resturant.addCustomer(temp);
		return resturant;
		
	}

	private static Resturant initializeResturantFromXml() {
		
		try {	
	         File inputFile = new File("configurationFile.xml");
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();
	         
	         
	         ////Restaurant
	         System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
	         System.out.println(doc.getDocumentElement().getNodeName() + " name :" + doc.getDocumentElement().getAttribute("name"));
	         Resturant returnRest = new  Resturant(doc.getDocumentElement().getAttribute("name"),
	        		 Integer.parseInt(doc.getDocumentElement().getAttribute("maxCustomersPerDay")),
	        		 Integer.parseInt(doc.getDocumentElement().getAttribute("numOfSeats")));
	         
	         Node nNode = doc.getElementsByTagName("Waiters").item(0);
	         Element eElement;
	         if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	        	 eElement = (Element) nNode;
	        	 returnRest.setMaxWaiters(Integer.parseInt(eElement.getAttribute("maxWaitersInShift")));
	        	 returnRest.setMaxCustomersPerWaiter(Integer.parseInt(eElement.getAttribute("maxCustomersCuncurrentlPerWaiter")));
	         }
	         NodeList nList = doc.getElementsByTagName("Customer");
//	         System.out.println("----------------------------  " + nList.getLength());
	         for (int temp = 0; temp < nList.getLength(); temp++) {
	            nNode = nList.item(temp);
//	            System.out.println("\nCurrent Element :" + nNode.getNodeName());
	            
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	               eElement = (Element) nNode;
	               Customer tempCustomer = new Customer(eElement.getAttribute("name"),eElement.getAttribute("whileWaiting"));
	               returnRest.addCustomer(tempCustomer);
	            }
	            
	         }
	         return returnRest;
	      } catch (Exception e) {
	         e.printStackTrace();
	      } 
		return null;
		
	}


}
