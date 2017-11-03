package ResultView;

import java.util.ArrayList;

import DataModel.FakeData;
import DataModel.InputData;
import java.util.ArrayList;
import java.util.HashMap;

import DataModel.FakeData;
import DataModel.InputData;

public class ResultTest {
	public static void main(String args[])
	 {
		int age[] = InputData.age;
		int annualSalary[] = InputData.annualSalary;
		double w0 = FakeData.initW0;
		double w1 = FakeData.initW1;
		double w2 = FakeData.initW2;
		double x0 = FakeData.initX0;
		double learningRate = InputData.learningRate;
		double threshold = FakeData.threshold;
		String result[] = InputData.result;
		ArrayList<Integer> goalValue = new ArrayList<>();
		ArrayList<Integer> fNet = new ArrayList<>();
		ArrayList<Double> net = new ArrayList<>();
		
		
		//test
		for (int i=0; age.length>i; i++){
			double test = 0;
			int value = 0;
			
			test = ((age[i]*288.2) + (annualSalary[i]*288.3) + (288.1*x0));
			
			System.out.println(test);
			
			if (test>= threshold){
				value = 1;
		}
			else
				value = 0;
			System.out.println(value);
			
		}
			
		for(int i1 =0; result.length>i1; i1++){
			int value1 = 0;
			if(result[i1].matches("approve"))
				value1 = 1;
			else
				value1 = 0;
			System.out.println(value1);
		}
	 }
}

		

