
public class Tester {

	public static void main(String[] args) {
		
		CarList myList = new CarList();
		
		// each thread searches a different website and adds 
		// cars to the CarList
		Thread t1 = new Thread(new CarsSearch(myList));
		Thread t2 = new Thread(new AutoTraderSearch(myList));
		Thread t3 = new Thread(new CarMaxSearch(myList));
		
		long startTime = System.currentTimeMillis();
		
		t1.start();
		t2.start();
		t3.start();
		
		try {
			t1.join();
			t2.join();
			t3.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long finishTime = System.currentTimeMillis() - startTime;
		
		System.out.println("Found " + myList.getCount() + " valid cars.");
		System.out.println("Search completed in " + 
		(finishTime / 1000) + "." +
		((finishTime % 1000) / 100) + 
		" seconds.");
	}

}
