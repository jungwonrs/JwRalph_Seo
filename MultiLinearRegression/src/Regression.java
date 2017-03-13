//for matrix multiplication. 
import java.util.ArrayList;
import java.util.HashMap;
import flanagan.io.*;
import flanagan.math.*;

public class Regression {
	//fake data
	
	
	/*
	 * If initial W is smaller than 0, cost is increasing...
	 * if initial W is bigger than 0, cost is decreasing...
	 * I don't know why
	 */
	
	static double w1=25;
	static double w2=20;
	static double w3=10;
	static double initB = 1;
	static double learningRate = 0.009;
	
	
	
	// input data
	static int [] yData = {152,185,180,196,142};
	static int [][]xData = new int [][] {{73,80,75},{93,88,93},{89,91,90},{96,98,100},{73,66,70}};
	static double [][]wData = new double [][] {{w1},{w2},{w3}};
	
	
	static HashMap<String, Double> wValue = new HashMap<String,Double>();
	static ArrayList<Double> costBucket = new ArrayList<Double>();
	static int cost;
	
	/*
	 * Goal - find out w1,w2,w3 when cost is zero(minimize).
	 */

	public static void main(String args[]){
		//add initial w1,w2 and w3 to HashMap 'wValue'
		
		wValue.put("w1", w1);	
		wValue.put("w2", w2);		
		wValue.put("w3", w3);		
	
		//int stop=0;
	while(true){
		
		//calculate multiplication of matrix
		Matrix matrixA = new Matrix(xData);
		Matrix matrixB = new Matrix(wData);
		Matrix matrixC = new Matrix(4,0); 
		
		double[][] result = new double [4][0];
		matrixC = matrixA.times(matrixB);
		result = matrixC.getArrayCopy();

		
		//initial h1 = x1w1+x2w2+x3w3+b
		double h0 = result[0][0]+initB;
		double h1 = result[1][0]+initB;
		double h2 = result[2][0]+initB;
		double h3 = result[3][0]+initB;
		double h4 = result[4][0]+initB;
		

		//Calculate cost
		
		//molecule
		double i = Math.pow((h0-yData[0]), 2)+Math.pow((h1-yData[1]), 2)+Math.pow((h2-yData[2]), 2)+Math.pow((h3-yData[3]), 2)+Math.pow((h4-yData[4]), 2);
		
		//cost 
		// changed data type for easy to check.
		cost = (int) (i/(result.length));

		
		//Calculate Gradient Descent  
		// new w = old w - learning Rate * partial differential of W value from the cost.  
		double newW1 = wValue.get("w1")-(learningRate*wValue.get("w1"));
		double newW2 = wValue.get("w2")-(learningRate*wValue.get("w2"));
		double newW3 = wValue.get("w3")-(learningRate*wValue.get("w3"));
		
		/*
		newW1 = (Math.round(newW1*100)/100);
		newW2 = (Math.round(newW2*100)/100);
		newW3 = (Math.round(newW3*100)/100);
		 */
		
		
		//add new w1, w2 and w3
		wValue.put("w1", newW1);
		wValue.put("w2", newW2);
		wValue.put("w3", newW3);
		w1 = wValue.get("w1");
		w2 = wValue.get("w2");
		w3 = wValue.get("w3");	
		wData = new double [][] {{w1},{w2},{w3}};
		
		
		//System.out.println("w1="+""+wValue.get("w1"));
		//System.out.println("w2="+""+wValue.get("w2"));
		//System.out.println("w3="+""+wValue.get("w3"));
		//System.out.println("cost="+""+cost);
		//stop++;
		
		//minimum 

		if (cost==1){
			System.out.println("w1="+""+wValue.get("w1"));
			System.out.println("w2="+""+wValue.get("w2"));
			System.out.println("w3="+""+wValue.get("w3"));
			System.out.println("cost="+""+cost);
		break;
		
		
		/*
		if (stop==1000){
			
			System.out.println("w1="+""+wValue.get("w1"));
			System.out.println("w2="+""+wValue.get("w2"));
			System.out.println("w3="+""+wValue.get("w3"));
			System.out.println("cost="+""+cost);

		break;
*/
		
		}
		

	}
	
	}
	
}

	

	
	
	
	
	
	
	
	
	
	
	
