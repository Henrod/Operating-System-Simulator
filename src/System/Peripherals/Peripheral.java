package System.Peripherals;

import Cells.jobTable;
import LinkedLists.QueueLinkedList;

/* this is an abstract class, which is mother of all peripherals */
public abstract class Peripheral {
	boolean occupied;	//tells if the peripheral is being used or not
	jobTable job_being_executed; 	//keeps the job which is being executed
	public QueueLinkedList queueLinkedList;	//Waiting queue for peripheral, when it is busy
	int overhead_time;
	
	abstract public boolean get_occupied();
	abstract public void executeJob(jobTable job);
	abstract public void finishJob();
	abstract public jobTable get_job();
	abstract public int get_overhead_time();
}
