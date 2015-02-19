package demo.genaticalgorithm.net;

public class Message{
	public static int MAX_NODES = 20;
	public boolean type = false;
	public int id;
	public int chromosomeId;
	public int target = 0;
	public int lifeTime = 0;
	public int lifeTimeCounter = 0;
	public int length = 0;
	public int[][] nodes = new int[MAX_NODES][2]; //source node is in this table as well
	public int spareLifeTime = 0;
		
	public Message(){
		for(int i = 0; i<nodes.length; i++){
			nodes[i][0]=-1;
		}
	}

	/*
	 * Copy the message
	 */
	public Message copy(){
		Message m = new Message();
		m.type = this.type;
		m.id = this.id;
		m.target = this.target;
		m.lifeTime = this.lifeTime;
		m.lifeTimeCounter = this.lifeTimeCounter;
		m.spareLifeTime = this.spareLifeTime;
		m.length = this.length;
		
		for(int i = 0; i<nodes.length; i++){
			System.arraycopy(this.nodes[i],0,m.nodes[i],0,2);
		}
		return m;
	}

	public void print(){
		System.out.println(""+type+"-"+lifeTime+"-"+lifeTimeCounter+"-"+length);
		for(int i =0; i < MAX_NODES; i++){
			System.out.println("{"+nodes[i][0]+"|"+nodes[i][1]+"|"+nodes[i][2]+"}");
		}
			
	}
}
