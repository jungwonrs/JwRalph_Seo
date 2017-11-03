//for matrix multiplication. 
import java.util.ArrayList;
import java.util.HashMap;
import flanagan.io.*;
import flanagan.math.*;
import weka.core.matrix.Matrix;

public class Regression {
	//fake data
	
	
	static double w1=0.001;
	static double w2=0.002;
	static double w3=0.003;
	static double initB = 1;
	static double learningRate = 0.000001;
	
	
	// input data
	static int [] yData = {152,185,180,196,142};
	static double [][] xData = new double [][] {{73,80,75},{93,88,93},{89,91,90},{96,98,100},{73,66,70}};
	static double [][]wData = new double [][] {{w1},{w2},{w3}};
	
	
	static HashMap<String, Double> wValue = new HashMap<String,Double>();
	static ArrayList<Double> costBucket = new ArrayList<Double>();
	static double cost;
	
	/*
	 * Goal - find out w1,w2,w3 when cost is zero(minimize).
	 */

	public static void main(String args[]){
		//add initial w1,w2 and w3 to HashMap 'wValue'
		
		wValue.put("w1", w1);	
		wValue.put("w2", w2);		
		wValue.put("w3", w3);		
	
		int stop=0;
		
		double x1,x2,x3;
		double sumx1=0, sumx2=0, sumx3=0;
		for (int i =0; i<xData.length; i++){
			x1 = xData[i][0];
			sumx1 += x1;
			x2 = xData[i][1];
			sumx2 += x2;
			x3 = xData[i][2];
			sumx3 += x3;
		}
	
	while(true){
		
		//calculate multiplication of matrix
		Matrix matrixA = new Matrix(xData);
		Matrix matrixB = new Matrix(wData);
		Matrix matrixC = new Matrix(4,0); 
		
		double[][] result = new double [4][0];
		matrixC = matrixA.times(matrixB);
		result = matrixC.getArrayCopy();

		
		//initial h1 = x1w1+x2w2+x3w3+b
		ArrayList<Double>hypothesis = new ArrayList<Double>();
		for (int i =0; i<result.length; i++){
			double hypo;
			hypo = result[i][0]+initB;
			hypothesis.add(hypo);
		}


		//Calculate cost
		double temp, sumtemp=0;
		for (int i =0; i<hypothesis.size(); i++){
			temp = Math.pow(hypothesis.get(i)-yData[i], 2);
			sumtemp += temp; 
		}
		
		cost = sumtemp/hypothesis.size();

		
		//Calculate Gradient Descent  
	
		ArrayList<Double> gd1 = new ArrayList<Double>();
		ArrayList<Double> gd2 = new ArrayList<Double>();
		ArrayList<Double> gd3 = new ArrayList<Double>();
		for (int i=0; i<hypothesis.size(); i++){
			double gdtemp1 =((hypothesis.get(i)-yData[i])*sumx1)/hypothesis.size();
			gd1.add(gdtemp1);
			double gdtemp2 = ((hypothesis.get(i)-yData[i])*sumx2)/hypothesis.size();
			gd2.add(gdtemp2);
			double gdtemp3 = ((hypothesis.get(i)-yData[i])*sumx3)/hypothesis.size();
			gd3.add(gdtemp3);
			
			double newW1, newW2, newW3;
			newW1 = (wValue.get("w1")-(learningRate*gd1.get(i)));
			newW2 = (wValue.get("w2")-(learningRate*gd2.get(i)));
			newW3 = (wValue.get("w3")-(learningRate*gd3.get(i)));
			
			wValue.put("w1", newW1);
			wValue.put("w2", newW2);
			wValue.put("w3", newW3);
			
			
		}
		
		wData = new double [][] {{wValue.get("w1")},{wValue.get("w2")},{wValue.get("w3")}};
		
	
		System.out.println("w1="+" "+wValue.get("w1"));
		System.out.println("w2="+" "+wValue.get("w2"));
		System.out.println("w3="+" "+wValue.get("w3"));
		System.out.println("cost="+" "+cost);
		System.out.println();
		stop++;
		
	
		
		if (stop==1000){
			
			System.out.println("End");
			
		break;
	
		}
		

	}
	      
	}

}
	
