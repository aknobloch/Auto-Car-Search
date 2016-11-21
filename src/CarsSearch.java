import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class CarsSearch implements Runnable {
	
	private CarList list;
	private String carsLink = "http://www.cars.com/for-sale/searchresults.action?feedSegId=28705"
			+ "&transTypeId=28112&sf2Nm=price&requestorTrackingInfo=RTB_SEARCH&yrId=51683&yrId=56007"
			+ "&yrId=34923&yrId=39723&yrId=47272&sf1Nm=miles&sf2Dir=DESC&PMmt=0-0-0&zc=30097&rd=100"
			+ "&mlgId=28863&prMn=0&sf1Dir=ASC&prMx=12000&searchSource=UTILITY&crSrtFlds=feedSegId-pseudoPrice"
			+ "&pgId=2102&rpp=250";
	int count = 1;
	
	
	public CarsSearch(CarList inList) {
		this.list = inList;
	}

	
	@Override
	public void run() {
		
		try {
			
			URL carsURL = new URL(carsLink);
			URLConnection connection = carsURL.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			
			String line;
			String carInfo = "";
			
			while((line = in.readLine()) != null) {
				// parse out the current car and all relevant info
				if(line.contains("detPhoto" + count)) {
					
					carInfo += line;
					
					for(int i = 0; i < 90; i++) {
						carInfo += in.readLine() + "\n";
					}
					// send info to method to parse and enter into database
					parseInfo(carInfo);
					// go to next car and clear carInfo
					count++;
					carInfo = "";
				}
			}
			
			System.out.println("Cars.com returned " + count + " vehicles.");
			in.close();
		
		} catch(IOException ie) {
			ie.printStackTrace();
			System.out.println("Error accessing Cars.com");
		}
	}

	private void parseInfo(String carInfo) {
		
		String modelYear;
		String car;
		String price;
		String mileage;
		String adLink;
		String picLink;
		
		
		// get car link
		adLink = carInfo.substring(carInfo.indexOf("href") + 6, carInfo.indexOf(">") - 1);
		adLink = "http://www.cars.com/" + adLink;
		
		// get photo link
		carInfo = carInfo.substring(carInfo.indexOf("data-def-src") + 14);
		picLink = carInfo.substring(0, carInfo.indexOf("\""));
		if(picLink.contains("ghostVehicle")) picLink = "imageNotFound.jpg";
		
		// get model year
		// truncate carInfo to begin at relevant point
		carInfo = carInfo.substring(carInfo.indexOf("modelYearSort") + 15);
		modelYear = carInfo.substring(0, carInfo.indexOf("<"));
		
		// get car
		carInfo = carInfo.substring(carInfo.indexOf("mmtSort") + 9);
		car = carInfo.substring(0, carInfo.indexOf("<"));
		
		// do not want fiats or versas
		String copyOfCar = car.toLowerCase();
		if(copyOfCar.contains("fiat") || copyOfCar.contains("versa")) return;
		
		// get price
		carInfo = carInfo.substring(carInfo.indexOf("priceSort") + 12);
		price = carInfo.substring(0, carInfo.indexOf("<"));
		
		// get mileage 
		carInfo = carInfo.substring(carInfo.indexOf("milesSort") + 11);
		mileage = carInfo.substring(0, carInfo.indexOf("<"));
		if(mileage.contains("mi.")) {
			mileage = mileage.substring(0,mileage.length()-4);
		}
		
		
		list.addCar(modelYear, car, price, mileage, adLink, picLink);
		
	}

}
