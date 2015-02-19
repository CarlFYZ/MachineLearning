package demo.genaticalgorithm.bugs;

import java.awt.*;

public class Bug{
	// Parameters
	static int numberOfGenes = 1024;
	static int numberOfBitsInGene = 18;
	static int mouthful = 50;
	static int moveTax  = 10;
	static int reproductionTax = 0;
	static int overallTax = 10;

	static int reproductionThresh = 1000;
	static int sexThresh = 800; //600
	static int offspringNum = 2;
	
	static double strategyDensity = 1;
	static double mutationRate = 0.1;
	static double crossoverFraction = 0.4;
	
	// Global variables
	boolean[][] chromosome = new boolean[numberOfGenes][numberOfBitsInGene];
	int[] geneUsageCounters = new int[numberOfGenes];
	int posX = 100;
	int posY = 100;
	int energy = 500;
	
	/*
	 * constructor
	 */
	Bug(int x, int y, boolean[][] aChromosome){
		chromosome = aChromosome;
		posX = x;
		posY = y;
	}

	/*
	 * constructor
	 */
	Bug(int x, int y){
		for (int i =0; i<1024; i++){
			for (int j =0; j<10; j++){
				chromosome[i][9-j] = ((i>>j)&1)==1?true:false;
			}

			if (Math.random() < strategyDensity){
				for (int j =0; j<8; j++){
					chromosome[i][10+j] = (Math.random()>.5)?true:false;
				}
			}
		}
		posX = x;
		posY = y;
	}


	/*
	 * one day passed
	 */
	public void oneDayPassed(){
		energy = energy - overallTax;
		// 1 move
		move();

		// 2 eat
		eat();
		
		// 3 reproduction
		if (energy > reproductionThresh)
			asexualReproduction();
		if (energy > sexThresh && Math.random() > crossoverFraction)
			sexualReproduction();			

	}

	/*
	 * How a bug moves
	 */
	public void move(){
		int index = convertBoolToInt(readSensors());
		//random move
		//index = (int)(Math.random()*(numberOfGenes));

		//System.out.print(" inf:" + index);

		boolean[] angleInBool = new boolean[4];
		System.arraycopy(chromosome[index],10,angleInBool,0,4);
		double angleInt = convertBoolToInt(angleInBool);
		double angle = 2*Math.PI * angleInt / 16;
		
		boolean[] stepsInBool = new boolean[4];
		System.arraycopy(chromosome[index],14,stepsInBool,0,4);
		int steps = convertBoolToInt(stepsInBool) + 0;
		// System.out.println(" sin:"+Math.sin(angle) + " step:" + steps);

		posY = (  posY - (int)Math.round((steps)*Math.sin(angle)) + MapOfFood.sizeOfMap)%MapOfFood.sizeOfMap;
		posX = (  posX + (int)Math.round((steps)*Math.cos(angle)) + MapOfFood.sizeOfMap)%MapOfFood.sizeOfMap;
		energy = energy - moveTax * steps;
		geneUsageCounters[index]++;
		
	}

	/*
	 * How a bug eats
	 */
	public void eat(){
		if (MapOfFood.foodTable[posX][posY] >= mouthful){
			MapOfFood.foodTable[posX][posY] = MapOfFood.foodTable[posX][posY] - mouthful;
			energy = energy + mouthful;
		}
		else{
			energy = energy + MapOfFood.foodTable[posX][posY];
			MapOfFood.foodTable[posX][posY] = 0;
			
		}

		
	}

	/*
	 * Asexual reproduction
	 */
	public void asexualReproduction(){
		//System.out.println("AAA");
		for(int i =0; i< offspringNum; i++){
			Bug aBug = new Bug(posX,posY,copyChromosome(chromosome));
			System.arraycopy(geneUsageCounters,0,aBug.geneUsageCounters,0,geneUsageCounters.length);
			mutation(aBug.chromosome,aBug.geneUsageCounters);
			MainFrame.registerABug(aBug);
		}
		energy = energy - reproductionTax;
	}
	
	/*
	 * Sexual reproduction
	 */
	public void sexualReproduction(){
		Bug aBug = null;
		int difX = 0;
		int difY = 0;
		for(int j=0; j<MainFrame.bugsV.size(); j++){
			aBug = (Bug)MainFrame.bugsV.elementAt(j);
			if(this.equals(aBug))
				continue;
			difX = Math.abs(aBug.posX - posX);
			difY = Math.abs(aBug.posY - posY);
			if (difX == MapOfFood.sizeOfMap -1)
				difX =1;
			if (difY == MapOfFood.sizeOfMap -1)
				difY =1;
			if (difX == 0 && difY <=1 || difY ==0 && difX <=1){
				//System.out.println("SSS");
				for(int i =0; i< offspringNum; i++){
					Bug newBug = new Bug(posX,posY);
					crossover(chromosome, geneUsageCounters, aBug.chromosome, aBug.geneUsageCounters,newBug);
					MainFrame.registerABug(newBug);
				}
				energy = energy - reproductionTax;
				break;
			}			
		}
	}
	
	/*
	 * mutation
	 */
	public static void mutation(boolean[][] aChromosome, int[] counters){
		for (int i = 0; i<numberOfGenes; i++){
			if (Math.random() < mutationRate){
			for (int j =10; j<numberOfBitsInGene; j ++){
					aChromosome[i][j] = (Math.random()>.5)?true:false;;
					counters[i] = 0;
				} 
			}
		}
	}

	/*
	 * crossover
	 */
	public static void crossover(boolean[][] chrom1, int[] count1,boolean[][] chrom2, int[] count2, Bug aBug){
		boolean[][] newChromosome = new boolean[numberOfGenes][numberOfBitsInGene];
		
		for(int i =0; i<numberOfGenes; i++){
			if(Math.random() > 0.5){
				System.arraycopy(chrom1[i],0,newChromosome[i],0,numberOfBitsInGene);
				aBug.geneUsageCounters[i] = count1[i];
			}else{
				System.arraycopy(chrom2[i],0,newChromosome[i],0,numberOfBitsInGene);
				aBug.geneUsageCounters[i] = count2[i];
			}
		}
		aBug.chromosome = newChromosome;
	}

	/*
	 * copy chromosome
	 */
	public static boolean[][] copyChromosome(boolean[][] aChromosome){
		boolean[][] newChromosome = new boolean[aChromosome.length][aChromosome[0].length];
		for(int i =0; i<aChromosome.length; i++){
			System.arraycopy(aChromosome[i],0,newChromosome[i],0,aChromosome[i].length);
		}
		return newChromosome;
	}

	/*
	 * A bug read its sensors to get the information of the world
	 */
	private boolean[] readSensors(){
	
		boolean[] info = new boolean[10];

		fillSensorValue(MapOfFood.foodTable[(posX-1+MapOfFood.sizeOfMap)%MapOfFood.sizeOfMap][posY],0,info);
		
		fillSensorValue(MapOfFood.foodTable[posX][(posY-1+MapOfFood.sizeOfMap)%MapOfFood.sizeOfMap],2,info);
		fillSensorValue(MapOfFood.foodTable[(posX+1+MapOfFood.sizeOfMap)%MapOfFood.sizeOfMap][posY],4,info);
		fillSensorValue(MapOfFood.foodTable[posX][(posY+1+MapOfFood.sizeOfMap)%MapOfFood.sizeOfMap],6,info);
		fillSensorValue(MapOfFood.foodTable[posX][posY],8,info);
		


		return info;
	}

	/*
	 * convert a boolean sting into integer
	 */
	public static int convertBoolToInt(boolean[] bools){
		int v = 0;
		for(int i=0; i< bools.length; i++){
			if (bools[i] == true)
				v = v + (int)(Math.pow(2,i));
		}
		return v;
	}

	/* 
	 * Fill the sensor
	 */
	public static void fillSensorValue(int v, int index, boolean[] booleans){
		if (v > MapOfFood.maxFood * .75){
			booleans[index]= booleans[index + 1] = true;
		}else if(v > MapOfFood.maxFood * .5){
			booleans[index]= true;
			booleans[index + 1]= false;
		}else if(v > MapOfFood.maxFood * .25){
			booleans[index]= false;
			booleans[index + 1]= true;
		}else{
			booleans[index]= booleans[index + 1] = false;
		}
	}

	/*
	 * Show the bug
	 */
	public void paint(Graphics g)
	{
		int unitSize = 4;
		g.setColor(Color.red);
		//g.drawLine(posX,posY,posX,posY);
		//g.drawOval(posX-(unitSize/2),posY-(unitSize/2),unitSize,unitSize);
		g.fillOval(posX-(unitSize/2),posY-(unitSize/2),unitSize,unitSize);
	}

}
