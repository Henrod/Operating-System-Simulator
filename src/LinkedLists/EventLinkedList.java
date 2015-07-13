package LinkedLists;

import Cells.eventTable;
import Cells.jobTable;

public class EventLinkedList {
	eventTable firstLink; //head of the list
	
	/* returns the head of the list */
	public eventTable getFirstEvent(){
		return firstLink;
	}
	
	/* Constructor of the list */
	public EventLinkedList(){
		firstLink = null;
	}
	
	/* Verifies if the list is empty */
	public boolean isEmpty(){
		return firstLink == null;
	}
	
	/* insert jobTable as a firstLink in the list */
	public void insertLink(eventTable newEvent){
		newEvent.nextEvent = firstLink;
		firstLink = newEvent;
	}
	
	/* gets the job that have the lesser arrival time and not in the queue, that means 
	 * the next job to be executed in the simulator */ 
	public eventTable getNextEvent(){
		if(!isEmpty()){
			eventTable aux = firstLink;		//aux will pass through all list
			eventTable nextEvent = null;	//contain the job with minimum arrival time
			int min_arrivel;
			
			while(aux != null && aux.get_in_queue()){ //goes on until find some event which is not in a queue
				aux = aux.nextEvent;
			}
			//if(aux != null){
				min_arrivel = aux.get_event_time();
				
				while(aux != null){
					if(!aux.get_in_queue()){
						if(min_arrivel >= aux.get_event_time()){
							min_arrivel = aux.get_event_time();
							nextEvent = aux;
						}
					}
					aux = aux.nextEvent;
					//System.out.println(aux == null);
				}
			//}
			return nextEvent;
		}
		
		return null;
	}
	
	
	/* gets the job that have the lesser arrival time, but it has
	 * to be greater than the time stipulated.
	 * This method is called when a event in a queue is called, but cant be executed yet,
	 * so the next event in the event list is called */ 
	/*public eventTable getNextValidEvent(int minimum_time_acceptable){
		eventTable aux = firstLink;			//aux will pass through all list
		eventTable nextEvent = null;	//contain the job with minimum arrival time
		int min_time = -1;	//initial value -1, means nothing, just a value
		int aux_time = firstLink.get_event_time();
		while(aux != null){
			if(aux_time >= minimum_time_acceptable){
				if(min_time == -1){ 		//in this case, aux_time is the first value taken greater than minimum
					min_time = aux_time; 	//then, this first value is set
					nextEvent = aux;
				} else {	//then, a time is already set 
					if(min_time > aux_time){
						min_time = aux_time;
						nextEvent = aux;
					}
				}
			}
			aux = aux.nextEvent;
		}
		return nextEvent;
	}
	*/
	
	/* Removes a specific eventTable of the list */
	public eventTable removeEvent(eventTable event){
		eventTable current = firstLink;
		eventTable previous = firstLink;
		
		while(current != event){
			if(current.nextEvent == null){
				return null;
			} else {
				previous = current;
				current = current.nextEvent;
			}
		}
		
		if(current == firstLink){
			firstLink = firstLink.nextEvent;
		} else {
			previous.nextEvent = current.nextEvent;
		}
		
		return current;
	}
	
	/* gets event from job */
	public eventTable getEventFromJob(jobTable job){
		eventTable current = firstLink;
		
		while(current != null){
			if(current.get_event_job() == job){
				return current;
			}
			
			current = current.nextEvent;
		}
		
		return null;
	}

	/* this method display all events on the list, with jobID, in_queue and time to occur */
	public void displayStatus(){
		eventTable event = firstLink;
		System.out.println("\nEvents on the list: ");
		while(event != null){
			System.out.println("	" +
					"Job ID: " + event.get_event_job().get_job_id() + 
					" | Event ID: " + event.get_event_identifier() +
					" | In queue: " + event.get_in_queue() + 
					" | Event time: " + event.get_event_time() + "ns" + 
					" | Record count: " + event.get_event_job().get_record_count());
			
			event = event.nextEvent;
		}
	}
}
