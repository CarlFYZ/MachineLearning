package demo.genaticalgorithm.simpleEv;

class SimpleEv{

	public static int nbTimes = 1;
	public static double probCrossover = 0.5;
	public static double probMutation = 0.01;
	
	public int sizeOfPopulation = 1000;
	public int lengthOfChromosome = 80;
	public int extraGeneration = 100;
	public int maxGeneration = extraGeneration + 800 + 100;
	public int generationId = 0;

	private boolean [][] chromosomesTable = new boolean[sizeOfPopulation][lengthOfChromosome];
	public int [][] usageTable = new int[sizeOfPopulation][lengthOfChromosome];
	public double [] fitnessTable = new double[sizeOfPopulation];
	public double [] cumulatedFitnessTable  = new double[sizeOfPopulation];
	public double [] cumulatedProbabilityTable = new double[sizeOfPopulation];
	public double [][] geneDistribution = new double[maxGeneration][lengthOfChromosome];
	public double [][][] genePairDistribution = new double[maxGeneration][lengthOfChromosome/2][4];
	public double [][] usageMap = new double[maxGeneration][maxGeneration+101];
	public boolean[] targetChromosome = new boolean[lengthOfChromosome];

	/**
	 * Main entry of SimpleEv
	 */
	public static void main(String args[])	{
		SimpleEv aGA = new SimpleEv();
		// 1 argument parsing
		if (2 == args.length){
			SimpleEv.probCrossover = Double.parseDouble(args[0]);
			SimpleEv.probMutation = Double.parseDouble(args[1]);
		}else if(0 < args.length){
			System.out.println("Usage: java SimpleEv \n       java SimpleEv Pc Pm");
			System.exit(0);
		}
		
		// 2 run GA and measure the average generation where all ones is discovered
		float averageGeneration = 0;
		for (int i =0; i<nbTimes; i++){
			averageGeneration = averageGeneration + aGA.go();
		}

		System.err.print("Average generation = " + (averageGeneration = averageGeneration/nbTimes) + " ");

	}
	
	/**
	 * run the simple GA
	 */
	public int go() {

		// 1 Generate the population
		chromosomesTable = generatePopulation(chromosomesTable,usageTable);
		double v1 = 0;

		
		for (int j=0; j<lengthOfChromosome; j++){
			targetChromosome[j] = true;
		}
		
		for(int i =0; i<maxGeneration; i++){

			geneDistribution = getGeneDistribution(chromosomesTable,i);
			//genePairDistribution = getGenePairDistribution(chromosomesTable,i);
			System.out.print("" + i + " ");
			v1 += printRate(geneDistribution,i);
			
			// 2 Calculate the Fitness
			
			if (i == extraGeneration + 200){
				
				int [] arr = new int[lengthOfChromosome];
				for(int k = 0; k<lengthOfChromosome; k++){
					arr[k] =k;
				}
				int myCache = 0;
				int index = 0;
				for(int k = lengthOfChromosome-1; k>0; k --){ 
					myCache = arr[k];
					index = (int)(Math.random() * (k*1.0));

					arr[k] = arr[index];
					arr[index] = myCache;
				}
				
				for (int j=0; j<64; j++){
					targetChromosome[arr[j]] = false;
				}
				
			}
			if (i == extraGeneration + 550){
				for (int j=0; j<lengthOfChromosome; j++){
					targetChromosome[j] = true;
				}
			}

			cumulatedFitnessTable = getCumulatedFitnessTable(chromosomesTable);


			// 2.1 Convert fitness table to cumulated probability table, so we can play Roolette wheel.
			cumulatedProbabilityTable = convertFitnessTbToCumulatedProbabilityTb(cumulatedFitnessTable);

			// 3 Repeat until sizeOfPopulation offspring are created
			getOffspring();
			//chromosomesTable = selection2(chromosomesTable,cumulatedProbabilityTable);

			// 4 replace current population with new population(crossover and mutation)
			
			generationId += 1;
			
			for(int j =0; j< sizeOfPopulation; j++){
				for(int k =0; k< lengthOfChromosome; k++){				
					try{
						usageMap[i][usageTable[j][k]]++;
					}catch(Exception e){
						System.out.println(" " +usageTable[j][k] + " " + i + " " + j + " " + k);
					}
				}
			}
			for(int j =0; j< maxGeneration; j++){
				System.out.print(" " + usageMap[i][j]);
			}
			System.out.println();
			
			
		}

		System.err.println("Max generation " + maxGeneration + " reached");
		System.exit(1);
		return maxGeneration;
	
	}

	/**
	 * Replace current population with new population
	 */
	public void getOffspring(){
		boolean [][] returnChromosomeTable = new boolean [sizeOfPopulation][lengthOfChromosome];
		int [][] returnUsageTable = new int [sizeOfPopulation][lengthOfChromosome];
		// Spin Roolette wheel sizeOfPopulation times
		for (int i = 0; i<sizeOfPopulation/2; i++){
			int parentA = tournamentSelection();
			int parentB = tournamentSelection();

			System.arraycopy(chromosomesTable[parentA],0,returnChromosomeTable[2*i],0,lengthOfChromosome);
			System.arraycopy(usageTable[parentA],0,returnUsageTable[2*i],0,lengthOfChromosome);

			System.arraycopy(chromosomesTable[parentB],0,returnChromosomeTable[2*i+1],0,lengthOfChromosome);
			System.arraycopy(usageTable[parentB],0,returnUsageTable[2*i+1],0,lengthOfChromosome);

			crossover(returnChromosomeTable[2*i],returnChromosomeTable[2*i+1],
						returnUsageTable[2*i],returnUsageTable[2*i+1],probCrossover);

			mutation(returnChromosomeTable[2*i],returnUsageTable[2*i],probMutation);
			mutation(returnChromosomeTable[2*i+1],returnUsageTable[2*i+1],probMutation);		
		}
		chromosomesTable = returnChromosomeTable;
		usageTable = returnUsageTable;
	}

	/**
	 * selection
	 * rolette-wheel
	 */
	int fitnessProportionateSelection(){
		double randNb = Math.random();
		// Select the chromosome where the ball stops
		for (int i = 0; i<sizeOfPopulation; i++){
			if (randNb < cumulatedProbabilityTable[i]){
				return i;
			}
		}
		System.err.println("error");
		return sizeOfPopulation-1;
	}

	int tournamentSelection(){
		int id1 = 0;
		int id2 = 0;
		id1 = (int)(Math.random()* sizeOfPopulation);
		id2 = (int)(Math.random()* sizeOfPopulation);
		if(fitnessTable[id1] > fitnessTable[id2]){
			return id1;
		}else{
			return id2;
		}
	}
		
	public double printRate(double[][] geneDistribution, int gId){
		double vDif = 0;
		if(gId >0){
			for(int i =0; i<lengthOfChromosome; i++){
				vDif += geneDistribution[gId][i] * Math.log(geneDistribution[gId][i]/geneDistribution[gId-1][i]);
				vDif += (1-geneDistribution[gId][i]) * Math.log((1-geneDistribution[gId][i])/(1-geneDistribution[gId-1][i]));
			}
		}
		
		System.out.print(" " + vDif);
		return vDif;
	}

	public double printPairRateRE(double[][][] genePairDistribution, int gId){
		double vDif = 0;
		if(gId >0){
			for(int i =0; i<lengthOfChromosome/2; i++){
				for(int j = 0; j<4; j++){				
					vDif += genePairDistribution[gId][i][j] * Math.log(genePairDistribution[gId][i][j]/genePairDistribution[gId-1][i][j]);
				}		
				
			}
		}
		
		System.out.print(" " + vDif);
		return vDif;
	
	}
	
	/**
	 * Generate the population randomly
	 */
	public double[][] getGeneDistribution(boolean[][] chromosomeTable, int gId){
		for (int i =0; i< lengthOfChromosome; i++){
			for (int j=0; j<sizeOfPopulation; j++){
				if(chromosomeTable[j][i]){
					geneDistribution[gId][i] += 1;
				}
			}
			geneDistribution[gId][i] = geneDistribution[gId][i]/chromosomeTable.length;
			if(geneDistribution[gId][i] == 0){
				System.err.println("get 0");
				geneDistribution[gId][i] = 1.0/2/chromosomeTable.length;
			}
			
			if(geneDistribution[gId][i] == 1){
				System.err.println("get 1");
				geneDistribution[gId][i] = 1.0 - 1.0/2/chromosomeTable.length;
			}	
		}
		return geneDistribution;
	}

	public double[][][]  getGenePairDistribution(boolean[][] chromosomeTable, int gId){
		for (int i =0; i< lengthOfChromosome/2; i ++){
			for (int j=0; j< sizeOfPopulation; j++){
				if(chromosomeTable[j][2*i] && chromosomeTable[j][2*i+1]){
					genePairDistribution[gId][i][0] += 1;
				}
				if(chromosomeTable[j][2*i] && (!chromosomeTable[j][2*i+1])){
					genePairDistribution[gId][i][1] += 1;
				}
				if((!chromosomeTable[j][2*i]) && chromosomeTable[j][2*i+1]){
					genePairDistribution[gId][i][2] += 1;
				}
				if((!chromosomeTable[j][2*i]) && (!chromosomeTable[j][2*i+1])){
					genePairDistribution[gId][i][3] += 1;
				}
			}
			
			for(int j=0; j<4; j++){
				genePairDistribution[gId][i][j] = genePairDistribution[gId][i][j]/sizeOfPopulation;
				if(genePairDistribution[gId][i][j] == 0){
					System.err.println("got 0");
					genePairDistribution[gId][i][j] = 1.0/2/chromosomeTable.length;
				}
			}
		}
		return genePairDistribution;
	}
	
	/**
	 * Generate the population randomly
	 */
	public boolean[][]  generatePopulation(boolean[][] chromosomeTable, int[][] usageTable){
		for (int i =0; i< chromosomeTable.length; i ++){
			for (int j=0; j<chromosomeTable[0].length; j++){
				chromosomeTable[i][j] = (Math.random() > 0.5);
				usageTable[i][j] = (int)(Math.random()*maxGeneration/10);
			}
		}
		return chromosomeTable;
	}


	/**
	 * crossover
	 */
	public void crossover(boolean[] c1, boolean[] c2, int[] u1, int[] u2, double prob){
		int lengthChromosome = c1.length;
		boolean[] cTemp = new boolean[c1.length];
		int[] usageTemp = new int[c1.length];
		// crossover with probability prob
		if (getProbabilitySample(prob)){
			// 1 seperate the chromosome, totally c1.length -1 possiable ways
			int seperator = Math.round ( (float)((c1.length -2)* Math.random()))+1;
			
			// 2.1 Copy c2-part2 to cTemp
			System.arraycopy(c2, seperator, cTemp, seperator,lengthChromosome-seperator);
			System.arraycopy(u2, seperator, usageTemp, seperator,lengthChromosome-seperator);
			
			// 2.2 Copy c1-part2 to c2, c2 is ok
			System.arraycopy(c1, seperator, c2, seperator,lengthChromosome-seperator);
			System.arraycopy(u1, seperator, u2, seperator,lengthChromosome-seperator);
			// 2.3 Copy cTemp-part2 to c1, c1 is ok
			System.arraycopy(cTemp,seperator, c1, seperator,lengthChromosome-seperator);
			System.arraycopy(usageTemp,seperator, u1, seperator,lengthChromosome-seperator);

		}
			
	}


	/**
	 * mutation
	 */
	public void mutation(boolean[] chrom, int[] usage,double prob){

		
		for (int i =0; i < chrom.length; i++){
			// reverse with probability prob
			if(getProbabilitySample(prob)){
				chrom[i] = !chrom[i];//Math.random()<0.5?false:true;
				usage[i] = 0;
				
			}
		}
	}


	/**
	 * convert fitness table to cumulated probability table
	 */
	public double[] convertFitnessTbToCumulatedProbabilityTb(double[] cumuFitnessTable){
		double [] cumuProbabilityTable = new double[cumuFitnessTable.length];
		for (int i=0; i<cumuFitnessTable.length; i++)	{

			double a1 = cumuFitnessTable[i];
			double a2 = cumuFitnessTable[cumuFitnessTable.length-1];
			cumuProbabilityTable[i] = a1/a2;
		}
		return cumuProbabilityTable;
	}

	/**
	 * calculate the fitnesses and save them into a table
	 */
	public double[] getCumulatedFitnessTable(boolean[][] chromosomeTable){
		double [] cumulFitnessTable  = new double [chromosomeTable.length];
		int cumulatedFitness = 0;
		int counter = 0;
		for (int i=0; i<chromosomeTable.length; i++){
			// calculate the fitness
			int fitnessV = getFitness(chromosomeTable[i],usageTable[i]);
			// If found the solution, return
			if (fitnessV == lengthOfChromosome){
				counter ++;
			}

			// calculate the cumulated fitness
			fitnessTable[i] = fitnessV;
			cumulatedFitness = cumulatedFitness + fitnessV;
			cumulFitnessTable[i] = cumulatedFitness;
		
		}
		//System.out.println("C = " + counter);
		if (counter > sizeOfPopulation*.9){
				//return null;
		}

		//System.out.println("Avg fitness = " + cumulFitnessTable[cumulFitnessTable.length-1]/chromosomeTable.length);
		return cumulFitnessTable;
	}

	/**
	 * fitness function
	 * f(x) =  number of ones(true) in a chromosome
	 */
	public int getFitness(boolean[] aChromosome, int[] usage)	{
		int returnValue = 0;
		
		for( int i = 0; i<lengthOfChromosome; i++)	{
			usage[i]++;
			if (aChromosome[i] == targetChromosome[i]){
				returnValue = returnValue + 1;
			}
		}
		if (returnValue > lengthOfChromosome-16){
			returnValue = lengthOfChromosome;
		}
		return returnValue;
	}

	/**
	 * get one sample from probability
	 */
	public boolean getProbabilitySample(double prob){
		return (Math.random() < prob);
	}

	/**
	 * print the Chromosome like following:
	 * for debug
	 * 0001110101110010101
	 */
	public void printChromosome(boolean[] aChromosome){
		String chrom = "";
		for (int i=0; i<aChromosome.length; i++){
			if (aChromosome[i])
				chrom = chrom + "1";
			else
				chrom =chrom + "0" ;
		}
		System.err.println(chrom);
	}
}
