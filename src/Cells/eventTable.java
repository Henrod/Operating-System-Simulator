package Cells;

/* This class is a cell for a linked list with all events in the system */
public class eventTable {
	int event_identifier;	//identifies the event, from 1 to 7
	int event_time;			//time of occurrence of the event
	jobTable event_job;		//job that will execute this event
	boolean in_queue;		//indicates if job is waiting is a queue
	Segment event_segment;  //event that is being executed in the events 2 and 3
	
	public eventTable nextEvent;	//next event of the linked list
	
	/* Constructor of the class */
	public eventTable(int event_identifier, int event_time, jobTable event_job){
		this.event_identifier = event_identifier;
		this.event_time = event_time;
		this.event_job = event_job;
		this.in_queue = false;
		this.event_segment = null;
	}
	
	public int get_event_time(){
		return event_time;
	}
	public void set_event_time(int time){
		this.event_time = time;
	}
	
	public int get_event_identifier(){
		return event_identifier;
	}
	public void set_event_identifier(int identifier){
		this.event_identifier = identifier;
	}
	
	public boolean get_in_queue(){
		return in_queue;
	}
	public void set_in_queue(boolean in_queue){
		this.in_queue = in_queue;
	}
	
	public jobTable get_event_job(){
		return event_job;
	}
	
	public Segment get_event_segment(){
		return this.event_segment;
	}
	public void set_event_segment(Segment segment){
		this.event_segment = segment;
	}
}
