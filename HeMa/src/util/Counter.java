package util;

public class Counter {

	public static int total = 0;
	public static int predicted = 0;
	public static float correct = 0;
	
	public static int gPredicted = 0;
	public static int gCorrect = 0;
	
	public static int sPredicted = 0;
	public static int sCorrect = 0;
	
	public static int mPredicted = 0;
	public static int mCorrect = 0;
	
	public static void print() {
		predicted = gPredicted + sPredicted + mPredicted;
		correct = gCorrect + sCorrect + mCorrect;
		
		System.out.println("total = " + total);
		System.out.println("predicted = " + predicted);
		System.out.println("correct = " + correct);
		System.out.println("precision = " + correct*1.0/predicted);
		System.out.println("recall = " + correct*1.0/total);
	}
}
