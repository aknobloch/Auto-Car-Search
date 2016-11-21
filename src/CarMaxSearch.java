import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class CarMaxSearch implements Runnable {
	
	private CarList list;
	private String carMaxLink = "http://www.carmax.com/search?ASc=5&D=50&zip=30097"
			+ "&N=285+283&sP=0-12000&sM=NA-60000&sY=2011-2016&Q=436b1a02-9739-47c4-a39f-836beef387db"
			+ "&Ep=search:results:results%20page";
	int count = 1;
	
	
	public CarMaxSearch(CarList inList) {
		this.list = inList;
	}
	
	
	@Override
	public void run() {
		
		try {
			
			URL carMaxURL = new URL(carMaxLink);
			URLConnection connection = carMaxURL.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			String carInfo = "";
			
			while((line = in.readLine()) != null) {
				
				if(line.contains("photo")) {
					
					carInfo += line + "\n";
					
					for(int i = 0; i < 60; i++) {
						
						carInfo += in.readLine() + "\n";
	
					}
					
					parseInfo(carInfo);
					count++;
					carInfo = "";
				}	
			}
			
			System.out.println("CarMax.com returned " + count + " vehicles.");
			
		} catch(IOException ie) {
			ie.printStackTrace();
			System.out.println("Error accessing CarMax.com");
		}
		
	}

	private void parseInfo(String carInfo) {
		
		String modelYear;
		String car;
		String price;
		String mileage;
		String link;
		String picLink;
		
		// do not want versas or fiats
		if(carInfo.contains("Versa") || carInfo.contains("Fiat")) return;
		
		// get pic link
		carInfo = carInfo.substring(carInfo.indexOf("src") + 5);
		picLink = carInfo.substring(0, carInfo.indexOf("\""));
		
		// get link
		carInfo = carInfo.substring(carInfo.indexOf("href=\"") + 6);
		link = "http://www.carmax.com" + carInfo.substring(0, carInfo.indexOf(";"));
		
		//System.out.println(link);
		
		// parse to correct place, get model year
		carInfo = carInfo.substring(carInfo.indexOf("h1") + 3);
		modelYear = carInfo.substring(0, carInfo.indexOf(" "));
		
		// parse to correct place, get car name
		carInfo = carInfo.substring(carInfo.indexOf(" ") + 1);
		car = carInfo.substring(0, carInfo.indexOf("<"));
		
		// parse to correct place, get mileage
		carInfo = carInfo.substring(carInfo.indexOf("dd") + 3);
		mileage = carInfo.substring(0, carInfo.indexOf("K")) + ",000";
		
		// parse to correct place, get price
		carInfo = carInfo.substring(carInfo.indexOf("$") + 1);
		price = carInfo.substring(0, carInfo.indexOf("<"));
		
		// price usually followed by * indicating no-haggle-price
		price = price.replaceAll("\\*", "");
		
		list.addCar(modelYear, car, price, mileage, link, picLink);

	}

}
