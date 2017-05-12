import java. util.ArrayList;
import java.util.HashMap;
import weka.core.matrix.Matrix;

public class XOR1 {
	// for saving W value
		static HashMap<String, Double>wValue = new HashMap<String,Double>();
		//define input
		
		
		
		private static Matrix multiply (double w_data1, double w_data2){
			double [][] x_data = {{0,0}, {0,1}, {1,0}, {1,1}};
			double [][] w_data = {{w_data1},{w_data2}};
			
			Matrix x = new Matrix (x_data);
			Matrix w = new Matrix (w_data);
			Matrix r = new Matrix (3,0);
			r = x.times(w);
			
			return r;
		}
		
		
		//temp1
		private static ArrayList<Double> temp1 (double wa1, double wa2) {
			//define bias
			double b = 0;
			
			//define w
			wValue.put("wa1", wa1);
			wValue.put("wa2", wa2);
			double [][] w_data = {{wValue.get("wa1")}, {wValue.get("wa2")}};
			
			//multiply x and w
			
			Matrix multi = multiply(wa1,wa2);
			double [][] result = new double [3][0];
			result = multi.getArrayCopy();
			
			//K(x)
			ArrayList<Double> sigmoid = new ArrayList<Double>();
			for (int i=0; i<result.length; i++){
				double hypo = (1/(1+Math.pow(Math.E, (-result[i][0]+b))));
				sigmoid.add(hypo);
			}
			return sigmoid;
		}
		
		//temp2
		private static ArrayList<Double> temp2 (double wb1, double wb2){
			//define bias
			double b = 0;
			
			//define w
			wValue.put("wb1", wb1);
			wValue.put("wb2", wb2);
			double [][] w_data = {{wValue.get("wb1")}, {wValue.get("wb2")}};
			
			//multiply x and w
		
			Matrix multi = multiply(wb1,wb2);
			double [][] result = new double [3][0];
			result = multi.getArrayCopy();
			
			//K(x)
			ArrayList<Double> sigmoid = new ArrayList<Double>();
			for (int i =0; i<result.length; i++){
				double hypo = (1/(1+Math.pow(Math.E, (-result[i][0]+b))));
				sigmoid.add(hypo);
			}
			return sigmoid;
		}
		
		private static double error (ArrayList temp1, ArrayList temp2, double wc1, double wc2){
			
			//define bias and goal
			double b = 0;
			double []Y = {0,1,1,0};
					
			// put temp1, temp2 as new x data
			double [][] x_data = 
				{{(double) (temp1.get(0)),(double) temp2.get(0)},
				{(double) (temp1.get(1)),(double) temp2.get(1)},
				{(double) (temp1.get(2)),(double) temp2.get(2)},
				{(double) (temp1.get(3)),(double) temp2.get(3)}};
				
				
			//define w 	
			wValue.put("wc1", wc1);
			wValue.put("wc2", wc2);
			double [][] w_data = {{wValue.get("wc1")}, {wValue.get("wc2")}};
			
			Matrix x = new Matrix(x_data);
			Matrix w = new Matrix(w_data);
			Matrix r = new Matrix(3,1);
			double [][] result = new double [3][1];
			r = x.times(w);
			result = r.getArrayCopy();
			
			//hypothesis;
			double cost = 0;
			double sumCost = 0;
			double error = 0;
			ArrayList<Double> sigmoid = new ArrayList<Double>();
			for (int i =0; i<result.length; i++){
				double hypo = (1/(1+Math.pow(Math.E, (-result[i][0]+b))));
				sigmoid.add(hypo);
				
				cost = -Y[i]*Math.log(sigmoid.get(i))-(1-Y[i])*Math.log(1-sigmoid.get(i));
				sumCost += cost;
				}
			error = sumCost/sigmoid.size();

			return error;
		}

		private static void backpropagation(double wa1,double wa2,double wb1,double wb2,double wc1,double wc2) {
			error(temp1(wa1,wa2),temp2(wb1,wb2),wc1,wc2);
			
			// learning Rate
			double lr = 0.000001;
			
			double [][] w_data = {{wValue.get("wc1")}, {wValue.get("wc2")}};
			
			Matrix multi = multiply(wc1,wc2);
			double [][] result = new double [3][1];
			result = multi.getArrayCopy();
			
			//derivative of wa and wb
			double [][] x_data = {{0,0}, {0,1}, {1,0}, {1,1}};
			double dwa1=0, dwa2=0, dwb1=0, dwb2 =0;
			for (int i = 0; i< x_data.length; i++){
				double Tdwa1 = x_data[i][0]*wValue.get("wc1");
				dwa1 += Tdwa1;
				
				double Tdwa2 = x_data[i][1]*wValue.get("wc1");
				dwa2 += Tdwa2;
			
				double Tdwb1 = x_data[i][0]*wValue.get("wc2");
				dwb1 += Tdwb1;
				
				double Tdwb2 = x_data[i][1]*wValue.get("wc2");
				dwb2 += Tdwb2;
			}
			
			//call temp1 and temp2 for derivative of wc
			double [][] tempArray = 
				{{(double) (temp1(wa1,wa2).get(0)),(double) temp2(wb1,wb2).get(0)},
				{(double) (temp1(wa1,wa2).get(1)),(double) temp2(wb1,wb2).get(1)},
				{(double) (temp1(wa1,wa2).get(2)),(double) temp2(wb1,wb2).get(2)},
				{(double) (temp1(wa1,wa2).get(3)),(double) temp2(wb1,wb2).get(3)}};
			
			double sumtemp1 = 0;
			double sumtemp2 = 0;
			for (int i = 0; i<tempArray.length; i++){
				double Tsumtemp1 = tempArray[i][0];
				sumtemp1 += Tsumtemp1;
				
				double Tsumtemp2 = tempArray[i][1];
				sumtemp2 += Tsumtemp2;
			
			}
		int stop = 0;
		
		while (true) {
			stop ++;
			
			double error = error(temp1(wa1,wa2),temp2(wb1,wb2),wc1,wc2);
		
			
			double newWa1 = wa1-lr*(dwa1);
			double newWa2 = wa2-lr*(dwa2);
			double newWb1 = wb1-lr*(dwb1);
			double newWb2 = wb2-lr*(dwb2);
			double newWc1 = wc1-lr*(sumtemp1);
			double newWc2 = wc2-lr*(sumtemp2);
		
			
			wValue.put("wa1", newWa1);
			wValue.put("wa2", newWa2);
			wValue.put("wb1", newWb1);
			wValue.put("wb2", newWb2);
			wValue.put("wc1", newWc1);
			wValue.put("wc2", newWc2);
			wa1 = newWa1;
			wa2 = newWa2;
			wb1 = newWb1;
			wb2 = newWb2;
			wc1 = newWc1;
			wc2 = newWc2;
			
			/*
			System.out.println("wa1="+"  "+wa1);
			System.out.println("wa2="+"  "+wa2);
			System.out.println("wb1="+"  "+wb1);
			System.out.println("wb2="+"  "+wb2);
			System.out.println("wc1="+"  "+wc1);
			System.out.println("wc1="+"  "+wc1);
			*/
			System.out.println("error="+"  "+error);
			
			//System.out.println("---------------------");
			
			/*
			if (stop==1000000){
				System.out.println("wa1="+"  "+wa1);
				System.out.println("wa2="+"  "+wa2);
				System.out.println("wb1="+"  "+wb1);
				System.out.println("wb2="+"  "+wb2);
				System.out.println("wc1="+"  "+wc1);
				System.out.println("wc1="+"  "+wc1);
				System.out.println("error="+"  "+error);
				
				System.out.println("error="+"  "+error);
			
			}
			 */
		}
		

	}
			
		public static void main (String[] args){
			

			backpropagation(1,2,3,4,5,6);
			
	}
		
	}