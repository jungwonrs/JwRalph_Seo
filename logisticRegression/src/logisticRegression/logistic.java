package logisticRegression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import weka.core.matrix.Matrix;



public class logistic {
	private static void Logistic(double[]Y) {
		HashMap<String, Double>wValue = new HashMap<String, Double>();
		double learningRate = 0.0000000001;
		double [][] x_data = {{30,166},{34,183},{203,23},{550,270},{199,30},{36,177},{150,19},{33,154},{190,40},{660,304},{594,298},{40,192},{497,314},{455,263},{166,30}} ;
		
		//initial w;
		double w1 = 0.01;
		double w2 = 0.02;
		wValue.put("w1", w1);
		wValue.put("w2", w2);
		
		//for gradient. 
		double x1,x2;
		double sumx1=0, sumx2=0;
		for (int i =0; i<x_data.length; i++){
			x1 = x_data[i][0];
			sumx1 += x1;
			x2 = x_data[i][1];
			sumx2 += x2;
		}
		
		//add matrix for W value
		double [][] w_data = {{wValue.get("w1")},{wValue.get("w2")}};

		// for while statement 
		int stop =0;
		
		
		
		while (true){
			//x times w
			Matrix x = new Matrix(x_data);
			Matrix w = new Matrix(w_data);
			Matrix r = new Matrix(14,0);
			double [][] result = new double [14][0];
			r = x.times(w);			
			result = r.getArrayCopy();
			
			//calculate hypothesis (sigmoid)
			ArrayList<Double> sigmoid = new ArrayList<Double>();
			for (int i=0; i<result.length; i++){
				double hypo;
				hypo = (1/(1+Math.pow(Math.E,(-result[i][0]))));
				sigmoid.add(hypo);
			}
			
			//calculate cost(error)
			
			double cost = 0;
			double sumCost = 0;
			double error= 0;
			for (int i=0; i<sigmoid.size(); i++){
				cost = -Y[i]*Math.log(sigmoid.get(i))-(1-Y[i])*Math.log(1-sigmoid.get(i));
				sumCost += cost;
			}
			error = sumCost/sigmoid.size();
			
			//gradient descent
			ArrayList<Double> gd1 = new ArrayList<Double>();
			ArrayList<Double> gd2 = new ArrayList<Double>();
			for (int i=0; i<sigmoid.size(); i++){
				double gdtemp1 = ((sigmoid.get(i)-Y[i])*sumx1)/sigmoid.size();
				gd1.add(gdtemp1);
				double gdtemp2 = ((sigmoid.get(i)-Y[i])*sumx2)/sigmoid.size();
				gd2.add(gdtemp2);
				
				double newW1, newW2;
				newW1 = (wValue.get("w1")-(learningRate*gd1.get(i)));
				newW2 = (wValue.get("w2")-(learningRate*gd2.get(i)));
			
				wValue.put("w1", newW1);
				wValue.put("w2", newW2);
			}	 
			
			//put new W value
	
			w_data = new double [][] {{wValue.get("w1")},{wValue.get("w2")}};
			
			stop++;
			
			//Find minimum Value
			
			String Finalw = "w1="+wValue.get("w1")+":"+"w2="+wValue.get("w2"); 
			
			HashMap<String, Double>finalResult = new HashMap<String, Double>();
			finalResult.put(Finalw, error);
		
		     Entry<String,Double> min = null;
		     for (Entry<String,Double> entry : finalResult.entrySet()) {
		    	 if (min == null || min.getValue() > entry.getValue()) {
		    		 min = entry;
		    	 }
		     }
			
			//show error is decreasing
		     /*
		     	System.out.println("cost="+" " +error);
				System.out.println("w1="+" " +wValue.get("w1"));
				System.out.println("w2="+" " +wValue.get("w2"));
				System.out.println();
				if(stop==1000){
					break;
				}
				*/
		     
		     
				if (stop==500000){
					String temp1 = min.getKey();
					double temp2 = finalResult.get(temp1);
							
					System.out.println(temp1);
					System.out.println("minimumcost="+" "+temp2);
			  
					break;
				
				}
				
		}
			
		}

	
	
	public static void main (String[] args){
		System.out.println("---------------person---------------");
		double [] person = new double [] {1,1,0,0,0,1,0,1,0,0,0,1,0,0,0};
		Logistic(person);
		System.out.println();	
		
		System.out.println("---------------snake---------------");
		double [] snake = new double [] {0,0,1,0,1,0,1,0,1,0,0,0,0,0,1};
		Logistic(snake);
		System.out.println();	
		
		System.out.println("---------------elephant---------------");
		double [] elephant = new double [] {0,0,0,1,0,0,0,0,0,1,1,0,1,1,0};
		Logistic(elephant);
		System.out.println();	
		
		
		
		
	}


}