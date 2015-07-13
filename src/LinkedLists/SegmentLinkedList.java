package LinkedLists;

import Cells.Segment;

public class SegmentLinkedList {
	Segment first_segment;
	
	public SegmentLinkedList(){
		first_segment = null;
	}
	
	public void insert(Segment segment){
		segment.next_segment = first_segment;
		first_segment = segment;
	}
	
	public Segment search(int segment_id){
		
		for(Segment current = first_segment; current != null; current = current.next_segment)
			if(current.get_id() == segment_id)
				return current;
		
		return null;
	}
	
	public void displaySegments(){
		System.out.println("	List of segments:");
		for(Segment current = first_segment; current != null; current = current.next_segment)
			System.out.println("	Segment " + current.get_id() + "-> space: " + current.get_space()/1000000.0 + " MB");
	}
	
	public int number_of_segments(){
		int count = 0;
		for(Segment current = first_segment; current != null; current = current.next_segment)
			count++;
		return count;
		
	}
}
