package System;

import Cells.emptyCM;
import Cells.jobCM;
import Cells.jobTable;
import LinkedLists.CMEmptyLinkedList;
import LinkedLists.CMJobLinkedList;
import LinkedLists.QueueLinkedList;

/* class representing the central memory CM */
public class CentralMemory {
	int memory_size;					//size of memory
	int relocation_time;				//time of relocation
	CMJobLinkedList cmJobLinkedList;	//linked list of jobs current in the CM
	CMEmptyLinkedList cmEmptyLinkedList;//Linked list of empty spaces in the CM
	public QueueLinkedList cmQueueLinkedList;	//Waiting queue for CM, when it has no sufficient space for job
	
	public CentralMemory(int memory_size, int relocation_time) {
		this.memory_size = memory_size;
		this.relocation_time = relocation_time;
		cmJobLinkedList = new CMJobLinkedList();
		cmEmptyLinkedList = new CMEmptyLinkedList(this.memory_size);
		cmQueueLinkedList = new QueueLinkedList(1000, 5);
	}
	
	/**
	 * 
	 * @param job
	 * @return false if there was no enough space, and true if the job was successfully allocated
	 */
	public boolean allocateJob(jobTable job){
		int space = job.get_job_space();
		emptyCM freeSpace = cmEmptyLinkedList.verifyEmptySpace(space); 
		if(freeSpace != null){
			/* add job into jobs linked list in the CM */
			int job_initial_address = freeSpace.get_initial_address();	//initial address coincide with free space's one 
			int job_final_address = job_initial_address + job.get_job_space() - 1;
			jobCM newJob = new jobCM(job_initial_address, job_final_address, job);
			cmJobLinkedList.insertLink(newJob);	//insert in the linked list
			
			/* update data of free space */
			if(freeSpace.get_final_address() == job_final_address) //in this case, the job occupied all the empty space
				cmEmptyLinkedList.removeEmptyCM(freeSpace); 	   //then, remove this empty space from the list
			else
				freeSpace.set_initial_address(job_final_address + 1); //if not, initial address of empty space is the 
																//very next of the last address of the job in the CM 
				
				return true;
		}
		
		return false;
	}
	
	public int get_relocation_time(){
		return relocation_time;
	}
	
	/* this control method, display addresses of empty space and occupied space */
	public void displayStatus(){
		System.out.println("\n-----Memory mapping-----");
		System.out.println("Empty spaces: ");
		for(emptyCM e = cmEmptyLinkedList.getFirstEmptyCM(); e != null; e = e.nextEmptyCM){
			System.out.println("	" + e.get_initial_address() + " - " + e.get_final_address());
		}
		
		System.out.println("Allocated spaces: ");
		for(jobCM j = cmJobLinkedList.getFirstJobCM(); j != null; j = j.nextJobCM){
			System.out.println("	" + j.get_initial_address() + " - " + j.get_final_address() + ": Job " + j.getJob().get_job_id());
		}
		System.out.println("------------------------");
	}
}