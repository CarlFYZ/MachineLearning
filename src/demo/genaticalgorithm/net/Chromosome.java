package demo.genaticalgorithm.net;

public class Chromosome{
	int group = -1;
	//int id = -1;
	int proportion = -1;
	int lifeTime = -1;

	double counterMessagePassed = 0;
	double counterMessageConfirmed = 0;
	double counterMessageLost = 0;
	double counterMessageSent = 0;
	double counterSpareLifeTime = 0;
	double perf = 0;

	double mutationRate = 0.005;

	public void clear(){
		counterMessagePassed = 0;
		counterMessageConfirmed = 0;
		counterMessageLost = 0;
		counterMessageSent = 0;
		counterSpareLifeTime = 0;
		perf = 0;
	}

	/*
	 * Copy the chromosome
	 */
	
	public Chromosome copy(){
		Chromosome chrom = new Chromosome();
		chrom.proportion = this.proportion;
		chrom.lifeTime = this.lifeTime;
		return chrom;
	}
	
	/*
	 * mutation
	 */
	public void mutate(){
		if(Math.random()< mutationRate){
			proportion = (int)(Math.random()*(Node.MAX_PROPORTION+1));
		}
		if(Math.random()< mutationRate){
			lifeTime = (int)(Math.random()*(Node.MAX_LIFETIME+1));
		}
	}

	/*
	 * calculate the performance
	 */
	public double perf(double sends){
		if (counterMessageConfirmed == 0){
			counterMessageConfirmed = Net.epsilon2;
		}
		if (counterMessagePassed == 0){
			counterMessagePassed = Net.epsilon1;
		}
		
		if(counterMessageConfirmed!=0 && counterMessageSent!=0){
			
			double perf = Net.c1*Math.log(counterMessageSent/counterMessageConfirmed) 
				+ Net.c2*(counterMessagePassed/counterMessageSent)
				+ Net.c3*(counterSpareLifeTime/counterMessageSent);

			return perf;
		}else{
			return 10000000;
		}
	}
}
