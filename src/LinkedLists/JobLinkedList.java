package LinkedLists;

import Cells.jobTable;

/* Linked List containing all jobs current in the system */
public class JobLinkedList {
	jobTable firstLink; //head of the list
	
	public jobTable getFirstJob(){
		return firstLink;
	}
	
	/* Constructor of the list */
	public JobLinkedList(){
		firstLink = null;
	}
	
	/* Verifies if the list is empty */
	public boolean isEmpty(){
		return firstLink == null;
	}
	
	/* insert jobTable as a firstLink in the list */
	public void insertLink(jobTable newJob){
		newJob.nextJob = firstLink;
		firstLink = newJob;
	}
	
	/* gets the job that have the lesser arrival time, that means 
	 * the next job to be executed in the simulator */ 
	public jobTable getNextJob(){
		jobTable aux = firstLink;		//aux will pass through all list
		jobTable nextJob = firstLink;	//contain the job with minimum arrival time
		int min_arrivel = aux.get_arrival_time();
		while(aux != null){
			if(min_arrivel > aux.get_arrival_time()){
				min_arrivel = aux.get_arrival_time();
				nextJob = aux;
			}
		}
		
		return nextJob;
	}
	
	public jobTable removeJob(jobTable job){
		jobTable current = firstLink;
		jobTable previous = firstLink;
		
		while(current != job){
			if(current.nextJob == null){
				return null;
			} else {
				previous = current;
				current = current.nextJob;
			}
		}
		
		if(current == firstLink){
			firstLink = firstLink.nextJob;
		} else {
			previous.nextJob = current.nextJob;
		}
		
		return current;
	}
}
