package System;

import Cells.Segment;
import LinkedLists.Multiprogramming;
import LinkedLists.QueueLinkedList;

/* this class represents the CPU of the system */
public class CPU {
	
	boolean maximum;	//tells if the CPU has maximum number of jobs or not
	
	//jobTable job_being_executed; 	//keeps the job which is being executed
	public Multiprogramming multiprogramming;	//keeps all jobs which are being executed in the cpu
	
	public QueueLinkedList cpuQueueLinkedList;	//Waiting queue for CPU, when it is occupied
	
	int overhead_time;	//time to release CPU
	
	int time_slice;		//time which each job will be executed in the cpu
	
	public CPU(int multiprogramming_level, int max_wait_queue, int max_lenght_queue, int overhead_time, int time_slice){
		this.maximum = false;
		this.multiprogramming = new Multiprogramming(multiprogramming_level);
		this.cpuQueueLinkedList = new QueueLinkedList(max_wait_queue, max_lenght_queue);
		this.overhead_time = overhead_time;
		this.time_slice = time_slice;
	}
	
	public boolean get_occupied(){
		return maximum;
	}
	
	public int get_overhead_time(){
		return this.overhead_time;
	}
	
	public void executeSegment(Segment segment){
		if(multiprogramming.support_more_segs())
			multiprogramming.insertSegment(segment);
		else
			maximum = true;	//if cpu doens't support more jobs, then is has maximum number of jobs being executed
	}
	public void finishSegment(Segment segment){
		multiprogramming.removeSegment(segment);
		this.maximum = false;	//if removed a job, than cpu has not maximum jobs, for sure.
	}
	
	public int get_time_slice(){
		return time_slice;
	}
}
