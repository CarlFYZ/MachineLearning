package demo.genaticalgorithm.net;

import java.io.RandomAccessFile;

public class Net{

	public static int NB_NODES = 7;
	public static Node[] NODES = new Node[NB_NODES];
	public static boolean[][] linkTable = new boolean[NB_NODES][NB_NODES];
	public static int NB_TIMEUNIT = 11000;
	public static double Traffic = 0;
	public static double Delivery = 0;
	public static double c1 = 5;
	public static double c2 = 1;
	public static double c3 = 1;
	public static RandomAccessFile file0 = null;
	public static RandomAccessFile file1 = null;
	public static double epsilon1 = 0.0001;
	public static double epsilon2 = 0;
	public static int period =400;

	/*
	 * Main entry
	 */
	public static void main(String [] args){
		c1 = Double.parseDouble(args[0]);
		c2 = Double.parseDouble(args[1]);
		c3 = Double.parseDouble(args[2]);

		// 1 new net
		// 1.1 new links
		linkTable[2][1] = true;
		linkTable[1][2] = true;
		
		linkTable[0][2] = true;
		linkTable[2][0] = true;
		
		linkTable[2][3] = true;
		linkTable[3][2] = true;

		linkTable[4][3] = true;
		linkTable[3][4] = true;
		
		linkTable[5][4] = true;
		linkTable[4][5] = true;

		linkTable[6][4] = true;
		linkTable[4][6] = true;
		
		linkTable[5][6] = true;
		linkTable[6][5] = true;

		/*linkTable[4][7] = true;
		linkTable[7][4] = true;
		
		linkTable[8][7] = true;
		linkTable[7][8] = true;
		linkTable[6][8] = true;
		linkTable[8][6] = true;
		linkTable[7][9] = true;
		linkTable[9][7] = true;
		*/

		
		// 1.2 new nodes		
		for(int i = 0; i<NB_NODES; i++){
			NODES[i] = new Node(i);
		}
		
		int[] b = new int[NB_NODES];
		int cache = 0;
		int index = 0;
		for(int j = 0; j< NB_NODES; j++){
			b[j]=j;
		}
		
		
		//run every node in one generation
		double aa = 0;
		double bb = 0;
		double cc = 0;
		
		for(int i = 0; i< NB_TIMEUNIT ;i++ ){
			//System.out.println("------GENERATION " + i + "------");

			if (i == 5000){
				linkTable[1][4] = true;
		    	linkTable[4][1] = true;
				linkTable[1][2] = true;
		    	linkTable[2][1] = true;
				linkTable[4][2] = true;
		    	linkTable[2][4] = true;
				linkTable[0][1] = true;
		    	linkTable[1][0] = true;


			}
			double nb_sends = 0;
			double nb_receive = 0;
			double nb_passes = 0;
			double nb_confirm = 0;
			
			// First half of a day
			for (int j = 0; j<NB_NODES; j++){
				NODES[j].readIn();
			}
			
			int c = 0;
			double sump = 0;
			// Second half of a day
			for (int j = 0; j<NB_NODES; j++){
				NodeResults nRes = NODES[j].sendOut();
				nb_sends = nb_sends + nRes.sends;
				nb_receive = nb_receive + nRes.successes;
				nb_passes = nb_passes + nRes.passes;
				nb_confirm = nb_confirm + nRes.confirmation;
				double p = localPerformance(nRes);
				if (p != 1000000){
					c ++;
					sump = sump + p;
				}
			}

			//
			if((i-2)%10 == 0){
				System.err.print(" " + i + " \t");
				if(nb_receive == 0){
					nb_receive = epsilon2;
				}
				if(nb_passes == 0 && c1!=0){
					nb_passes = epsilon1;
				}
				
				if (nb_receive!=0&&nb_sends!=0){
					
					double performance = c1*Math.log(nb_sends/nb_receive) + c2*(nb_passes/nb_sends);
					double sumpro[] = new double[NB_NODES];
					double sumlife[] = new double[NB_NODES];

					for(int jj = 0; jj<Net.NB_NODES; jj++){
						for(int ii = 0; ii<Node.MAX_CHROMOSOME_PER_GROUP; ii++){
							sumpro[jj] = sumpro[jj] + NODES[jj].chromosomes[ii].proportion;
							sumlife[jj] = sumlife[jj] + NODES[jj].chromosomes[ii].lifeTime;
						}
						System.err.print("" + (int)(sumpro[jj]/Node.MAX_CHROMOSOME_PER_GROUP) + "/" + sumlife[jj]/Node.MAX_CHROMOSOME_PER_GROUP +" ");
					}
					System.err.println();
					System.out.print(" " + -sump/c);
					System.out.println("  " + -performance);
				}
			}
		}
	}

	/*
	 * Calculate the local performance
	 */
	public static double localPerformance(NodeResults nodeResults){
		double P = 1000000;
		if (nodeResults.passes == 0){
			nodeResults.passes = Net.epsilon1;
		}
		if (nodeResults.confirmation ==0){
			nodeResults.confirmation = Net.epsilon2;
		}
		
		if(nodeResults.passes !=0 && nodeResults.confirmation !=0){
			double T = nodeResults.passes/nodeResults.sends;
			double D = nodeResults.confirmation/nodeResults.sends; 
			double L = nodeResults.spareLifeTime/nodeResults.sends;
			
			P = Net.c1*Math.log(1/D) + Net.c2*T + Net.c3*L;
		}else{
			//do nothing
		}
		return P;
	}
	
	
	public static void trace(int level, String s){
		try{
			switch (level){
				case 0:
					file0.writeBytes(s);
					break;
				case 1:
					file0.writeBytes(s);
					break;
				default:
					System.out.println("err");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
