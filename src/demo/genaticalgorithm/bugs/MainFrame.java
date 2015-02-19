package demo.genaticalgorithm.bugs;

import java.awt.*;
import java.util.*;

public class MainFrame extends java.applet.Applet implements Runnable
{

	public static int nuBugs = 100;
	public static Vector<Bug> bugsV = new Vector<Bug>();
	public static int timeLimit = 2400;
	public static int usageLimit = 1000;
	public static float map[][] = new float[timeLimit][usageLimit];
	public static float normalizedMap[][] = new float[timeLimit][usageLimit];
	public static float persistence[][] = new float[timeLimit][usageLimit];
	public static double genesDistribution[][][]  = new double[2][Bug.numberOfGenes*2][16];

	/*
	 *	Called when initial/applet loaded
	 */
	public void init()
	{
		
		

		for (int i =0; i< nuBugs; i++){
			bugsV.add(new Bug(50,50));
			//bugs[i]= new Bug(50, 50);
		}

		new Thread(this).start();
		//	bug = new Bug(100,100);

		setBackground(Color.black);
		validate();
	}

	/*
	 *Called when repaint
	 */
	public void paint(Graphics g)
	{

		Image myOffscreenImage = createImage(MapOfFood.sizeOfMap+4,MapOfFood.sizeOfMap+15+4);
		Graphics offscreenGraphics = myOffscreenImage.getGraphics(); 

		MapOfFood.paint(offscreenGraphics);
		for (int i =0; i< bugsV.size(); i++)
			((Bug)(bugsV.elementAt(i))).paint(offscreenGraphics);
			
		offscreenGraphics.setColor(Color.red);
		g.drawImage(myOffscreenImage,0,0,this);
		
		//g.drawString("Strategic bugs",10,15);
		
	}


	/////////////////////////////////////////////////
	//              Thread main function           //
	/////////////////////////////////////////////////

	public void run()
	{
		int sleepTime = 1;
		int timeCounter = 0;
		for(timeCounter = 0; timeCounter < timeLimit; timeCounter++){
			
			try
			{
		    	Thread.sleep(sleepTime);
		    }
		    catch(InterruptedException e)
		    {}
			
			MapOfFood.oneDayPassed();
			Bug aBug = null;
			int vSize = bugsV.size();
			System.err.print("" + timeCounter + "[" + vSize + "]  " );

			
			//System.out.print("" + timeCounter + " " + vSize );

			try{
				for (int i =0; i< vSize; i++){
					//System.out.println("index " + i); 
					aBug = ((Bug)(bugsV.elementAt(i)));
					aBug.oneDayPassed();
					
					for(int j=0; j<aBug.geneUsageCounters.length; j++){
							//System.out.print("aa" + Math.round(aBug.geneUsageCounters[j]/bugsV.size()/Bug.numberOfGenes));
							//System.out.print(" " + j + "-" + Math.round(aBug.geneUsageCounters[j]));
							map[timeCounter][Math.round(aBug.geneUsageCounters[j])]++;
							normalizedMap[timeCounter][Math.round(aBug.geneUsageCounters[j])] = 
								map[timeCounter][Math.round(aBug.geneUsageCounters[j])]/vSize*nuBugs;
					}
					//System.out.print(";");
				}
			}catch(ArrayIndexOutOfBoundsException e){
				
				e.printStackTrace();
			}

			for(int i=usageLimit-1; i>=0; i--){
				for(int j= 0;j<usageLimit-i;j++){
					persistence[timeCounter][i] = persistence[timeCounter][i] + normalizedMap[timeCounter][i+j];
				}
			}
			
			Iterator it = bugsV.iterator();
			while(it.hasNext()){
				Bug b = (Bug)it.next();
				// System.out.print(" e=" + b.energy);
				if (b.energy <= 0){
					it.remove();
				}								
			}

			repaint();
		
			
			if(bugsV.size() <= 0)
				sleepTime = 300000;
			
			
			for(int j=0; j<usageLimit; j++){
				System.out.print(" " + map[timeCounter][j]);
			}

			System.out.println("");
		}
		
	}

	/*
	 * Test codes
	 */
	public void getGenesDistribution(int gId){
		Bug aBug = null;
		int vSize = bugsV.size();
		int gId2 = gId%2;
		try{
				int type = 0;
				for (int i =0; i< vSize; i++){
					//System.out.println("index " + i); 
					aBug = ((Bug)(bugsV.elementAt(i)));
					for(int index = 0; index<Bug.numberOfGenes; index++){
						boolean[] gene = new boolean[4];
						System.arraycopy(aBug.chromosome[index],10,gene,0,4);
						type = Bug.convertBoolToInt(gene);
						genesDistribution[gId2][index*2][type] ++;
						System.arraycopy(aBug.chromosome[index],14,gene,0,4);
						type = Bug.convertBoolToInt(gene);
						genesDistribution[gId2][index*2+1][type] ++;
					}
				}
		}catch(ArrayIndexOutOfBoundsException e){				
				e.printStackTrace();
		}

		for(int i = 0; i<Bug.numberOfGenes*2; i ++){
			for(int j=0; j<16; j++){
				genesDistribution[gId2][i][j] = genesDistribution[gId2][i][j]/vSize;
				if(genesDistribution[gId2][i][j] == 0){
					//System.err.print("got 0");
					genesDistribution[gId2][i][j] = 1.0/2/vSize;
				}
			}
		}

	}
	
	/*
	 * Test codes
	 */
	public double printRate(int gId){
		int idEarly = (gId+1)%2;
		int idAfter = gId%2;
		double vDif = 0;
		if(gId >0){
			for(int i =0; i<Bug.numberOfGenes*2; i++){
				for(int j = 0; j<16; j++){				
					//System.err.print(" a " + genePairDistribution[gId][i][j] + " b " + genePairDistribution[gId-1][i][j]);
					vDif += genesDistribution[idAfter][i][j] * 
					Math.log(genesDistribution[idAfter][i][j]/genesDistribution[idEarly][i][j]);
				}		
				
			}
		}
		
		System.out.print(" " + vDif + " ");
		//System.out.println(" " + geneDistribution[gId][i] + " " + geneDistribution[gId-1][i]);
		return vDif;
	
	}
	

	/*
	 * Register a bug in the vector
	 */
	public static void registerABug(Bug aBug){
		bugsV.add(aBug);
	}

	///////////////////////////////////////////////////////
	//              main  entry                          //
	///////////////////////////////////////////////////////
	public static void main(String args[])
	{
		/*int[][] aint= new int[2][3];
		int[][] aaint= new int[2][3];
		aint[1][1] = 3;
		System.arraycopy(aint,0,aaint,0,2);
		aint[1][1] = 2;
		System.out.println("AA" + aaint[1][1]);
		*/
		MainFrame gameWindow = new MainFrame();
		Frame f = new Frame();
		gameWindow.init();
		f.add("Center",gameWindow);
		//gameWindow.start();
		MapOfFood.initial();
		f.setSize(MapOfFood.sizeOfMap+4+4,MapOfFood.sizeOfMap+15+4+15);
		f.show();
	}

}

