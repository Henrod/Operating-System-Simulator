package LinkedLists;

import Cells.Segment;
import Cells.jobTable;

/* this class is a circular linked list with all process being executed in the cpu */
public class Multiprogramming {
	Segment first;	//first of the linked list
	
	public int max_segs;	//maximum number of jobs being executed at once
	public int number_of_segs; //number of jobs current being executed by the cpu
	
	public Multiprogramming(int max_jobs) {
		first = null;
		
		this.max_segs = max_jobs;
		this.number_of_segs = 0;
	}
	
	public void insertSegment(Segment segment){
		number_of_segs++;
		segment.next_segment = first;
		first = segment;	
	}
	
	//return TRUE if the number of jobs in cpu is LESS than max
	public boolean support_more_segs(){
		if(number_of_segs < max_segs) return true;
		
		return false;
	}
	
	public Segment removeSegment(Segment segment){
		Segment current = first;
		Segment previous = first;
				
		while(current != null){
			
			if(current.get_id() == segment.get_id() && current.get_job_id() == segment.get_job_id()){
				
				if(current == first){
					number_of_segs--;
					first = first.next_segment;
					return first;
				} else {
					number_of_segs--;
					previous.next_segment = current.next_segment;
					return current;
				}
			}
			
			previous = current;
			current = current.next_segment;
			
		}
		
		return null;
	}
	
	//return TRUE if job is being executed now, false otherwise
	public boolean get_seg_being_executed(Segment segment){
		Segment current = first;
		
		while(current != null){
			
			if(current.get_job_id() == segment.get_job_id()){
				return true;
			}
			
			current = current.next_segment;
			
		}
		return false;
	}
	
	public void display_segs_in_cpu(){
		System.out.println("\nSegments being executed in the CPU:");
		String segs = "";
		
		for(Segment current = first; current!= null; current = current.next_segment){
			segs = segs + " - Segment " + current.get_id() + " Job " + current.get_job_id();
		}
		
		System.out.println(segs);
	}
	
}
