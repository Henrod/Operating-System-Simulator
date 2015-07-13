package LinkedLists;

import Cells.Segment;
import Cells.jobCM;
import Cells.jobTable;

/* This list contains all jobs allocated in the CM */
public class CMJobLinkedList {
	jobCM firstLink; //head of the list
	
	public jobCM getFirstJobCM(){
		return firstLink;
	}
	
	/* Constructor of the list */
	public CMJobLinkedList(){
		firstLink = null;
	}
	
	/* Verifies if the list is empty */
	public boolean isEmpty(){
		return firstLink == null;
	}
	
	/* insert jobCM as a firstLink in the list */
	public void insertLink(jobCM newJobCM){
		newJobCM.nextJobCM = firstLink;
		firstLink = newJobCM;
	}
	
	public jobCM removeSegmentCM(Segment segment){
		jobCM current = firstLink;
		jobCM previous = firstLink;
		
		while(current.get_segment_id() != segment.get_id() || current.getJob().get_job_id() != segment.get_job_id()){
			if(current.nextJobCM == null){
				return null;
			} else {
				previous = current;
				current = current.nextJobCM;
			}
		}
		
		if(current == firstLink){
			firstLink = firstLink.nextJobCM;
		} else {
			previous.nextJobCM = current.nextJobCM;
		}
		
		return current;
	}
	
	public boolean segment_already_allocated(jobTable job, int segment_id){
		for(jobCM current = firstLink; current != null; current = current.nextJobCM)
			if(current.get_segment_id() == segment_id && current.getJob() == job)
				return true;
		
		return false;
	}
}
