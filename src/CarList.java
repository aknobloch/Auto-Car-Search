import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class CarList {
	
	private File htmlFile;
	private int count = 0;
	
	public CarList() {
		
		// create a File and write the HTML header to it
		htmlFile = new File("foundCars.html");
		
		try( PrintWriter out = new PrintWriter(htmlFile)) 
		{
			out.write("<!DOCTYPE html>" + "\n" +
					  "<html>" + "\n" +
					  "<body>" + "\n" + 
					  "<p align=\"center\">" + "\n" + "\n" + "\n");
		} catch (FileNotFoundException e) {
			System.out.println("Error writing to file.");
			e.printStackTrace();
		}
		
	}
	
	public synchronized void addCar(String modelYear, String car, String price, String mileage, String adLink, String picLink) {
		
		WarrantyCalculator calcWarranty = new WarrantyCalculator(modelYear, car, mileage);
		
		// dont want vehicles less than 12 months on warranty
		if(calcWarranty.monthsLeftOnWarranty() < 12) return;
		
		try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(htmlFile, true)))) 
		{
			// write pic line
			out.write("<img src=\"" + picLink +"\" alt=\"Car Picture\" style=\"width:225px;height:150px;\""
					+ "<br>" + "<br>" + "\n");
			// write car title line
			out.write("<a href=\"" + adLink + "\"target=\"_blank\">" + modelYear + " " 
					  + car + "</a>" + "<br>" + "\n");
			// write warranty line
			out.write(calcWarranty.monthsLeftOnWarranty() + " months left on warranty." + "<br>" + "\n");
			// write mileage line
			out.write(mileage + " miles" + "<br>" + "\n");
			// write price line
			out.write("$" + price + "<br>" + "\n" + "\n" );
			
			count++;
			
		} catch (IOException e) {
			System.out.println("Error writing to file.");
			e.printStackTrace();
		}
	}
	
	public int getCount() {
		return this.count;
	}
	
}
