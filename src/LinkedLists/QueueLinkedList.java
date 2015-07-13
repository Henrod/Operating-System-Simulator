package LinkedLists;

import System.ListHeader;
import Cells.Queue;
import Cells.jobTable;

public class QueueLinkedList {
	Queue firstLink; 		//head of the list
	ListHeader listHeader; //object which controls this queue's statistics
	
	/* Constructor of the list */
	public QueueLinkedList(int max_wait, int max_length){
		firstLink = null;
		listHeader = new ListHeader(firstLink, max_wait, 0, 0, max_length, 0, 0, 0, 0, 0);
	}
	
	public Queue getFirstLink(){
		return firstLink;
	}
	
	/* Verifies if the list is empty */
	public boolean isEmpty(){
		return firstLink == null;
	}
	
	/* insert Queue as a firstLink in the list */
	public void insertLink(Queue newLink){
		newLink.nextLink = firstLink;
		firstLink = newLink;
		
		/* when inserting a link in the queue, its length is incremented by one */
		int actual_length = listHeader.get_actual_length() + 1;
		/* partial length is the length of the queue multiplied by the time it existed = time arrived - time last change */
		int partial_lenght = (listHeader.get_actual_length()) * (newLink.get_time() - listHeader.get_last_time());
		
		listHeader.updateListHeader(newLink, 0, actual_length, partial_lenght, newLink.get_time(), true);
	}
	
	/* gets the link that have higher priority and lesser time_in */ 
	public Queue getNextLink(){
		Queue aux = firstLink;		//aux will pass through all list
		Queue nextLink = firstLink;	//contain the link with higher priority
		int priority = nextLink.get_priority();
		int time_in = nextLink.get_time();
		while(aux != null){
			if(aux.get_priority() > priority){
				priority = aux.get_priority();
				time_in = aux.get_time();
				nextLink = aux;
			} else if (aux.get_priority() == priority && aux.get_time() < time_in){
				time_in = aux.get_time();
				nextLink = aux;
			}
			
			aux = aux.nextLink;
		}
		
		return nextLink;
	}
	
	public Queue removeLink(jobTable job, int TIME){
		Queue current = firstLink;
		Queue previous = firstLink;
		
		while(current.get_job() != job){
			if(current.nextLink == null){
				return null;
			} else {
				previous = current;
				current = current.nextLink;
			}
		}
		
		if(current == firstLink){
			firstLink = firstLink.nextLink;
		} else {
			previous.nextLink = current.nextLink;
		}
		
		int new_wait = TIME - current.get_time();
		int actual_length = listHeader.get_actual_length() - 1;
		int partial_lenght = (listHeader.get_actual_length()) * (TIME - listHeader.get_last_time());
		
		listHeader.updateListHeader(firstLink, new_wait, actual_length,
				partial_lenght, TIME, false);
		
		return current;
	}
	
	public ListHeader getListHeader(){
		return listHeader;
	}

}
