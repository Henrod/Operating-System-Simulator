import java.io.IOException;

import System.Simulator;


public class Main {
	
	public static void main(String[] args) throws IOException{
		Simulator simulator = new Simulator();
		
		/* while there are jobs in the list of jobs in the system, execute their events */
		while(!simulator.endOfJobs()){
			simulator.selectEvent();
		}
		
		simulator.printEstatistics(); //end of simulation
	}
}
