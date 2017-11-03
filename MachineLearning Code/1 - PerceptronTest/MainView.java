package ResultView;

import CalculateController.Calculator;


public class MainView {
	public static void main(String args[])
	 {
		
		double calculator = Calculator.call();
		
		System.out.println(calculator);
		
	 }
}
