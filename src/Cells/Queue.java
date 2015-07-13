package Cells;

/* This class is cell for a linked list which contains all the
 * process that are waiting for using some peripheral or the CPU */
public class Queue {
	jobTable job;	//job which is waiting its time to use peripheral or CPU
	int priority;	//priority order the sequence of use
	int time_in;	//time of arriving in the queue
	
	public Queue nextLink;
	
	public Queue(jobTable job, int priority, int time_in){
		this.job = job;
		this.priority = priority;
		this.time_in = time_in;
	}
	
	public int get_priority(){
		return priority;
	}
	public int get_time(){
		return time_in;
	}
	public jobTable get_job(){
		return job;
	}
}
