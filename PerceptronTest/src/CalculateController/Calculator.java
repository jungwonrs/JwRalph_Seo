 package CalculateController;

import java.util.HashMap;
import DataModel.FakeData;
import DataModel.InputData;

public class Calculator {
	static int age[] = InputData.age;
	static int annualSalary[] = InputData.annualSalary;
	static double initw0 = FakeData.initW0;
	static double initw1 = FakeData.initW1;
	static double initw2 = FakeData.initW2;
	static double x0 = FakeData.initX0;
	static double learningRate = InputData.learningRate;
	static double threshold = FakeData.threshold;
	static String result[] = InputData.result;
	static HashMap<String, Double> wValue= new HashMap<String, Double>();
	static HashMap<Integer, Double> net = new HashMap<Integer, Double>();
	static HashMap<Integer, Integer> goalValue = new HashMap<Integer, Integer>();
	static HashMap<Integer, Integer> fNet = new HashMap<Integer, Integer>();

	
	// Reference : http://untitledtblog.tistory.com/27
	
	public static void NetCalculator(){
	
	// Define result: approve = 1, result: deny = 0
	// result is a goal value of each index
		for(int index = 0; index<result.length; index++){
			
			if(result[index].matches("approve"))
				goalValue.put(index,1);	
				
			else
				goalValue.put(index, 0);
		}
			
	
	// Add index	
	for(int index = 0; index<result.length; index++){
	
	// Add initial value of each 'W'
		wValue.put("w0",initw0);
		wValue.put("w1",initw1);
		wValue.put("w2",initw2);
	
	// Calculate first net 
	// net = w1x1 + w2x2 +b
	// b = w0x0
		net.put(index,(age[index]*wValue.get("w1")) + (annualSalary[index]*wValue.get("w2")) + (wValue.get("w0")*x0));
	
		
	// If Net is bigger than Threshold, f(net) = 1
	// if Net is smaller than Threshold, f(net) = 0
		if (net.get(index)>=threshold)
			fNet.put(index,1);
		if (net.get(index)<threshold)
			fNet.put(index,0);
	
		
	// f(net) value and goal value should be same	
	if (fNet.get(index) != goalValue.get(index)){
			while(true){
				wValue.put("w0",(wValue.get("w0") + (learningRate*x0*(threshold-net.get(index)))));
				wValue.put("w1",(wValue.get("w1") + (learningRate*x0*(threshold-net.get(index)))));
				wValue.put("w2",(wValue.get("w2") + (learningRate*x0*(threshold-net.get(index)))));	
				
				
				initw0=wValue.get("w0");
				initw1=wValue.get("w1");
				initw2=wValue.get("w2");
				
				net.put(index,(age[index]*wValue.get("w1")) + (annualSalary[index]*wValue.get("w2")) + (wValue.get("w0")*x0));
				
				if (net.get(index)>=threshold)
					fNet.put(index,1);
				if (net.get(index)<threshold)
					fNet.put(index,0);
				
				//Stop while
				if (fNet.get(index) == goalValue.get(index)){
				} break;
			}
			
		}
	
	// If f(net) value and goal value are different
	else
	{
		initw0=wValue.get("w0");
		initw1=wValue.get("w1");
		initw2=wValue.get("w2");
		
		net.put(index,(age[index]*wValue.get("w1")) + (annualSalary[index]*wValue.get("w2")) + (wValue.get("w0")*x0));
		
		if (net.get(index)>=threshold)
			fNet.put(index,1);
		if (net.get(index)<threshold)
			fNet.put(index,0);
		
		
	}
	
	// Result
	System.out.println("index"+""+index+" "+"w0-"+" "+wValue.get("w0"));
	System.out.println("index"+""+index+" "+"w1="+" "+wValue.get("w1"));		
	System.out.println("index"+""+index+" "+"w2="+" "+wValue.get("w2"));
	System.out.println("index"+""+index+" "+"net="+" "+net.get(index));	
	System.out.println("index"+""+index+" "+"fnet="+" "+fNet.get(index));
	System.out.println("index"+""+index+" "+"goal="+" "+goalValue.get(index));
	
	
	
	}
	
	
	}
		

					
public static double call(){

	NetCalculator();
	
	// return??????
		return 100;
}
}
