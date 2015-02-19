package demo.genaticalgorithm.net;

import java.util.*;

public class Node{
	public static int MAX_NEIGHBOUR = Net.NB_NODES - 0;
	public static int MAX_CHROMOSOME_PER_GROUP = 50;
	public static int MAX_LIFETIME = 10;
	public static int MAX_PROPORTION = 100;
	public static int GEN_MESSAGE_PER_DAY = 50;

	public static double crossoverRate = 0.2;

	public Vector pipe1 = new Vector();
	public Vector pipe2 = new Vector();
	public Vector input_pipe = pipe1;
	public Vector inter_pipe = pipe2;
	
	public int id = -1;
	public int lastChromId = -1;
	public Chromosome[] chromosomes = new Chromosome[MAX_CHROMOSOME_PER_GROUP];
	
	private int dayCounter = 0;
	private int messageCounter = 1;
	public NodeResults nodeResults = new NodeResults();
	public int nodesLastIdsBufferSize = 2000;
	public int[][] nodesLastIds = new int[Net.NB_NODES][nodesLastIdsBufferSize];
	public int[] nodesLastIdsCounter = new int[Net.NB_NODES];

	private int lifeTime = 0;

	/*
	 * constructor
	 */
	Node(int aId){
		this.id = aId;

		// new Chromosomes
		for(int j = 0; j< MAX_CHROMOSOME_PER_GROUP; j++){
			chromosomes[j] = new Chromosome();
			//chromosomes[j].id = j;
			chromosomes[j].proportion = (int)(Math.random() * (MAX_PROPORTION+1));
			chromosomes[j].lifeTime = (int)(Math.random()*(Node.MAX_LIFETIME+1));
		}
	}


	/*
	 * read in messages
	 */
	public void readIn(){
		if(input_pipe == pipe1){
			input_pipe = pipe2;
			inter_pipe = pipe1;
		}
		else{
			input_pipe = pipe1;
			inter_pipe = pipe2;
		}
	}

	/*
	 * send out messages
	 */
	public NodeResults sendOut(){

		nodeResults.clear();
		//1 generate messages & send
		for(int i=0; i<GEN_MESSAGE_PER_DAY; i ++){
			int target = (int)(Math.random()*(Net.NB_NODES-1));//Possiable send to self, TBC
			if(target >= this.id){
				target = target+1;
			}

			if(generateMessage(target)){
				nodeResults.sends++;
			}else{
				System.err.println("abnormale");
			}
		}

		//2 receive messages and send
		receiveAndForward();

		if(dayCounter%Net.period == 0){
			//fitness of
			selection();
			//mutate crossover
			mutation();
			crossover();
			
			for(int j = 0; j< MAX_CHROMOSOME_PER_GROUP; j++){
				chromosomes[j].clear();
			}

		}
		
		dayCounter ++;
		return nodeResults;
	}

	/*
	 * selection function
	 */
	public void selection(){
		for(int i =0; i< MAX_CHROMOSOME_PER_GROUP; i++){
			int ran1 = (int)(Math.random()*MAX_CHROMOSOME_PER_GROUP);
			int ran2 = (int)(Math.random()*MAX_CHROMOSOME_PER_GROUP);
			if(chromosomes[ran1].perf(nodeResults.sends) < chromosomes[ran2].perf(nodeResults.sends)){
				chromosomes[ran2] = chromosomes[ran1].copy();
			}else{
				chromosomes[ran1] = chromosomes[ran2].copy();
			}
		}
	}

	/*
	 * mutation
	 */
	public void mutation (){
		for(int i =0; i< MAX_CHROMOSOME_PER_GROUP; i++){
			chromosomes[i].mutate();
		}
	}

	/*
	 * crossover
	 */
	public void crossover(){
		int cache = 0;
		for(int i =0; i< MAX_CHROMOSOME_PER_GROUP; i++){
			if(crossoverRate > Math.random()){
				int ran1 = (int)(Math.random()*MAX_CHROMOSOME_PER_GROUP);
				int ran2 = (int)(Math.random()*MAX_CHROMOSOME_PER_GROUP);
				
				if(Math.random()>0.5){
					cache =	chromosomes[ran1].proportion;
					chromosomes[ran1].proportion =	chromosomes[ran2].proportion;
					chromosomes[ran2].proportion = cache;
				}
				if(Math.random()>0.5){
					cache =	chromosomes[ran1].lifeTime;
					chromosomes[ran1].lifeTime = chromosomes[ran2].lifeTime;
					chromosomes[ran2].lifeTime = cache;
				}
			}
		}
	}

	/*
	 * generate messages per day
	 */
	public boolean generateMessage(int target){
		Message m = new Message();
		m.type = true;
		m.id = messageCounter;
		////@_@System.out.println("     ");//"ct" + m.id);
		m.target = target;
		//m.lifeTime = lifeTime;
		m.lifeTimeCounter = 0;
		m.length = 0;

		int cId = (int)(Math.random()*MAX_CHROMOSOME_PER_GROUP);
		lastChromId = cId;
		
		////@_@System.out.print("#"+cId+"#"+messageCounter);
		messageCounter = messageCounter<50000-1?messageCounter+1:1;
		return sendToNeighbours(m,cId);

	}

	/*
	 * receive messages from inter pipe and forward
	 */
	public void receiveAndForward(){
		Enumeration e = inter_pipe.elements();
		while(e.hasMoreElements()){
			Message m = (Message)e.nextElement();
			if(m.type){

				if (m.target == this.id){
					if(isTheFirstMessage(m.nodes[0][0],m.id)){
						//@_@System.out.println("@");						
						nodeResults.successes++;
						m.type = false;
						m.spareLifeTime = m.lifeTime - m.lifeTimeCounter;

						sendToNeighbours(m);
					}else{
						//do nothing
					}
				}else{

					sendToNeighbours(m);
				}
			}else{//confirmation
				if(m.length == 0){
					chromosomes[m.nodes[m.length][1]].counterMessageConfirmed ++;
					chromosomes[m.nodes[m.length][1]].counterSpareLifeTime += m.spareLifeTime;
					nodeResults.spareLifeTime += m.spareLifeTime;
					nodeResults.confirmation ++;
				}
				else{
					if(m.nodes[m.length][1] >=0){
					}
					sendToNeighbours(m);
				}
			}
		}
		inter_pipe.removeAllElements();
	}

	/*
	 * send message to neighbours
	 */
	public void sendToNeighbours(Message m){
		if(m.type){ // normal message
			if(m.lifeTimeCounter < m.lifeTime){
				// pick a chromosome
				int cId = (int)(Math.random()*MAX_CHROMOSOME_PER_GROUP);
					
				// search, send on probability
				Chromosome chrom = chromosomes[cId];
				boolean b = false;
				for(int i=0; i<Net.NB_NODES; i++){
					if(targetIsNeighbour(i) && chrom.proportion > Math.random()*MAX_PROPORTION){
						Message newM = m.copy();
						newM.nodes[newM.length][0] = this.id;
						newM.nodes[newM.length][1] = cId;
						newM.length++;
						newM.lifeTimeCounter ++;
						////@_@System.out.print("[to:" + i + "]");
						chrom.counterMessagePassed ++;
						b = true;
						send(i,newM);
					}
					
				}
			}else{
				//@_@System.out.println("T");
			}// lifeTime reached
		}else{// feedback
			//m.print();
			m.length --;
			if(targetIsNeighbour(m.nodes[m.length][0])){
				//@_@System.out.println("                    ..." + m.nodes[m.length][0]);
				send(m.nodes[m.length][0],m);
			}else{
				//do nothing
			}
		}
	}

	/*
	 * send message to neighbours
	 */
	public boolean sendToNeighbours(Message m, int cId){
		// search, send on probability

		Chromosome chrom = chromosomes[cId];
			
		boolean b = false;
		for(int i=0; i<Net.NB_NODES; i++){
			if(targetIsNeighbour(i) && chrom.proportion > Math.random()*MAX_PROPORTION){
				////@_@System.out.print("(" + i + ")");					
				Message newM = m.copy();
				
				newM.nodes[newM.length][0] = this.id;
				newM.nodes[newM.length][1] = cId;
				newM.lifeTime = chrom.lifeTime;
				if(newM.lifeTime == 0){
					return true;
				}
				newM.length++; // =1
				newM.lifeTimeCounter ++;// before it is 0, now it is ++, TBC
				//System.err.print("(S2)");
				//newM.print();
				chrom.counterMessagePassed ++;
				
				b = true;
				send(i,newM);
			}else{
				////@_@System.out.println("-|");
			}
		}
		chrom.counterMessageSent ++;
		return true;		
	}


	public boolean targetIsNeighbour(int tId){
		return Net.linkTable[id][tId];		
	}
	
	public void send(int tId, Message m){
		Net.NODES[tId].input_pipe.add(m);
		if(m.type){
			nodeResults.passes++;
		}
	}

	/*
	 * Check if the message is the first message
	 * It is possiable that several copies of the same
	 * message will be recieved by the destination node,
	 * only the first one should be concerned
	 */
	public boolean isTheFirstMessage(int tId, int mId){
		boolean b = true;
		for(int i =0; i< nodesLastIdsBufferSize; i++){
			if(nodesLastIds[tId][i] == mId){
				return false;
			}
		}
		
		if(b){
			nodesLastIds[tId][nodesLastIdsCounter[tId]] = mId;
			nodesLastIdsCounter[tId] = nodesLastIdsCounter[tId] < nodesLastIdsBufferSize-1?nodesLastIdsCounter[tId]+1:0;
		}
			
		return b;
	}

	/*
	 * calculate the local performance of a node
	 */
	public void localPerformance(){
		if (nodeResults.passes == 0){
			nodeResults.passes = Net.epsilon1;
		}
		if (nodeResults.confirmation ==0){
			nodeResults.confirmation = Net.epsilon2;
		}
		if(nodeResults.passes !=0 && nodeResults.confirmation !=0){
			double T = nodeResults.passes/nodeResults.sends;
			double D = nodeResults.confirmation/nodeResults.sends; 
			double P = Net.c1*Math.log(1/D) + Net.c2*Math.log(T);
		}else{
			//do nothing
		}
	}
}
