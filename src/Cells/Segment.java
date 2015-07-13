package Cells;

/* this class represents the segmented jobs, so each job is made by its segments */
public class Segment {
	int segment_id;
	int space;
	int job_id;	//job that has this segment
	
	/* each job has its list of segments, so it maust contain the next cell for the linked list */
	public Segment next_segment;
	
	public Segment(int segment_id, int space, int job_id){
		this.segment_id = segment_id;
		this.space = space;
		this.job_id = job_id;
		
		this.next_segment = null;
	}
	
	public int get_id(){
		return segment_id;
	}
	
	public int get_space(){
		return space;
	}
	
	public int get_job_id(){
		return job_id;
	}
}
