package demo.genaticalgorithm.net;

public class NodeResults{
	double sends = 0;
	double passes = 0;
	double successes = 0;
	double confirmation = 0; 
	double lost = 0;
	double spareLifeTime = 0;

	/*
	 * clear the results
	 */
	void clear(){
		sends = 0;
		passes = 0;
		successes = 0;
		confirmation = 0;
		lost = 0;
		spareLifeTime = 0;
	}
	
	/*
	 * print for debug
	 */
	void println(){
		System.out.println("S=" + sends + " P=" + passes + " R=" + successes + " C=" + confirmation);
	}
}
