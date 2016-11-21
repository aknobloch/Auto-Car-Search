import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class AutoTraderSearch implements Runnable {
	
	private CarList list;
	private String autoTraderLink = "http://www.autotrader.com/cars-for-sale/cars+under+12000/Duluth+GA-30097"
			+ "?endYear=2015&maxMileage=60000&maxPrice=12000&searchRadius=75&showcaseOwnerId=57027721"
			+ "&sortBy=mileageASC&startYear=2011&transmissionCode=MAN&transmissionCodes=MAN&Log=0&numRecords=100";
	int count = 0;
	
	
	public AutoTraderSearch(CarList inList) {
		this.list = inList;
	}

	
	@Override
	public void run() {

		try {
			
			URL autoTraderURL = new URL(autoTraderLink);
			URLConnection connection = autoTraderURL.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String line;
			String carInfo = "";

			while((line = in.readLine()) != null ) {
				
				// if line starts relevant info, grab the relevant block of 
				// code and send it to parseInfo method.
				if(line.contains("atcui-truncate ymm")) {
					
					carInfo += line;
					for(int i = 0; i < 100; i++) {
						carInfo += in.readLine() + "\n";
					}
					
					parseInfo(carInfo);
					// go to next car and clear info
					count++;
					carInfo = "";
				}
			}
			
			System.out.println("AutoTrader.com returned " + count + " vehicles.");
			in.close();
	
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("Bad URL for Autotrader.com");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error accessing Autotrader.com");
		}
		
		
	}

	private void parseInfo(String carInfo) {
		
		String modelYear;
		String car;
		String price;
		String mileage;
		String adLink;
		
		// premium listings do not have mileage
		if(carInfo.indexOf("class=\"mileage\"") == -1) return;
		
		// truncate carInfo to begin at relevant info
		carInfo = carInfo.substring(carInfo.indexOf(">") + 1 );
		carInfo = carInfo.substring(carInfo.indexOf(">") + 1 );
		carInfo = carInfo.substring(carInfo.indexOf(" ") + 1 );
		
		// get model year, then truncate 
		modelYear = carInfo.substring(0, carInfo.indexOf(" "));
		carInfo = carInfo.substring(carInfo.indexOf(" "));
		
		// get car, then truncate
		car = carInfo.substring(1, carInfo.indexOf("<")) + " (AT)";
		carInfo = carInfo.substring(carInfo.indexOf("data-original=\"http") + 15);
		
		// do not want fiats or versas
		String copyOfCar = car.toLowerCase();
		if(copyOfCar.contains("fiat") || copyOfCar.contains("versa")) return;
		
		// get picLink
		adLink = carInfo.substring(0, carInfo.indexOf("\""));
		
		// truncate, then get price
		carInfo = carInfo.substring(carInfo.indexOf("$") + 1);
		price = carInfo.substring(0, carInfo.indexOf("<"));
		
		// truncate, then get mileage
		carInfo = carInfo.substring(carInfo.indexOf("class=\"mileage\""));
		carInfo = carInfo.substring(carInfo.indexOf(">") + 1 );
		carInfo = carInfo.substring(carInfo.indexOf(">") + 1 );
		mileage = carInfo.substring(0, carInfo.indexOf("<"));
		
		list.addCar(modelYear, car, price, mileage, autoTraderLink, adLink);
		
	}
	
}
