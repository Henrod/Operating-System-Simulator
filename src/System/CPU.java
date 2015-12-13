package System;

import Cells.jobTable;
import LinkedLists.QueueLinkedList;

/* this class represents the CPU of the system */
public class CPU {
	boolean occupied;	//tells if the CPU is being used or not
	jobTable job_being_executed; 	//keeps the job which is being executed
	public QueueLinkedList cpuQueueLinkedList;	//Waiting queue for CPU, when it is occupied
	int overhead_time;	//time to release CPU
	
	public CPU(){
		this.occupied = false;
		this.job_being_executed = null;
		this.cpuQueueLinkedList = new QueueLinkedList(1000, 5);
		this.overhead_time = 10;
	}
	
	public boolean get_occupied(){
		return occupied;
	}
	
	public int get_overhead_time(){
		return this.overhead_time;
	}
	
	public void executeJob(jobTable job){
		this.job_being_executed = job;
		this.occupied = true;
	}
	public void finishJob(){
		this.job_being_executed = null;
		this.occupied = false;
	}
	
	public jobTable get_job_being_executed(){
		return job_being_executed;
	}
}
