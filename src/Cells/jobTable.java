package Cells;

/* This class is a cell for a linked list with all jobs in the system */
public class jobTable {
	int arrival_time;		//time o arrive for the job
	int job_space;			//space occupied for the job in the CM
	int request_time;		//time between requests for I/O
	int total_cpu_time;		//total time used in cpu
	int record_count;	//number of I/O requests
	int[] peripherals;		//number of the peripheral being used
	int[] record_lenghts;	//time for executing each I/O
	boolean flag;			//interruption flag
	int time_remaining;		//time remaining to process after interruption
	int priority;			
	
	int jobID;	
	
	public jobTable nextJob; 	//Link to the next jobTable on the jobs linked list
	
	public jobTable(int jobID, int arrival_time, int job_space, int total_cpu_time, int record_count, 
		int[] peripherals, int[] record_lenghts, boolean flag, int time_remaining, int priority){
		this.jobID = jobID;
		this.arrival_time = arrival_time;
		this.job_space = job_space;
		this.total_cpu_time = total_cpu_time;
		this.record_count = record_count; 
		this.request_time = this.total_cpu_time/this.record_count;
		this.peripherals = peripherals;
		this.record_lenghts = record_lenghts;
		this.flag = flag;
		this.time_remaining= time_remaining;
		this.priority = priority;
	}
	
	public void retrieveData(){
		System.out.println("Job data: ");
		System.out.println("	Job Identifier: " + this.jobID);
		System.out.println("	Arrival time: " + this.arrival_time);
		System.out.println("	Job space: " + this.job_space);
		System.out.println("	Total CPU time: " + this.total_cpu_time);
		System.out.println("	Number of I/O requests: " + this.record_count);
		System.out.println("	Interrequest time: " + this.request_time);
		for(int i = 0; i < this.record_count; i++)
			System.out.println("	Peripheral[" + i + "] type: " + this.peripherals[i]);
		for(int i = 0; i < this.record_count; i++)
			System.out.println("	Record lenght " + i + ": " + this.record_lenghts[i]);
		System.out.println("	Interruption flag: " + this.flag);
		System.out.println("	Time remaining after interruption: " + this.time_remaining);
		System.out.println("	Priority: " + this.priority);		
	}
	
	public int get_arrival_time(){
		return arrival_time;
	}
	
	public int get_job_space(){
		return job_space;
	}
	
	public int get_priority(){
		return priority;
	}
	
	public int get_request_time(){
		return request_time;
	}
	
	public int get_record_count(){
		return record_count;
	}
	public void decrement_record_count(){
		this.record_count--;
	}
	
	public int get_job_id(){
		return jobID;
	}
	
	public int[] get_peripherals(){
		return this.peripherals;
	}
	
	public int[] get_record_lengths(){
		return this.record_lenghts;
	}
	
	public int get_time_remaining(){
		return this.time_remaining;
	}
	public void set_time_remaining(int time_remaining){
		this.time_remaining = time_remaining;
	}
	
	public boolean get_flag(){
		return this.flag;
	}
	public void set_flag(boolean flag){
		this.flag = flag;
	}
}
 