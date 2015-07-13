package Cells;

/* This class represents a job allocated in the central memory */
public class jobCM {
	int initial_address;	//initial address of job in central memory
	int final_address;		//final address of job in central memory
	jobTable job;			//corresponding job allocated
	int segment_id;			//corresponding segment allocated
	
	public jobCM nextJobCM;		//next cell of the linked list
	
	public jobCM(int initial_address, int final_address, jobTable job, int segment_id){
		this.initial_address = initial_address;
		this.final_address = final_address;
		this.job = job;
		this.segment_id = segment_id;
	}
	
	public jobTable getJob(){
		return job;
	}
	public int get_initial_address(){
		return initial_address;
	}
	public int get_final_address(){
		return final_address;
	}
	public int get_segment_id(){
		return segment_id;
	}
}
