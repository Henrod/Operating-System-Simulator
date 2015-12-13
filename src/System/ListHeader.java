package System;

import Cells.Queue;

public class ListHeader {
	Queue queue;	//queue which this list controls
	int max_wait;	//maximum wait for a job in the queue
	int total_wait; //sum of the waits of all jobs in the queue
	float mean_wait;//mean wait of jobs in the queue during simulation
	
	int max_lenght;		//maximum number of jobs supported by the queue
	int actual_length; 	//actual length of the queue
	int total_length; 	//total length multiplied by the time it remained that length
	float mean_length; 	//mean length of the queue during simulation
	
	int last_link; 	//time of last insertion or remotion
	int number;		//total number of jobs in the queue during simulation
	
	public ListHeader(Queue queue, int max_wait, int total_wait, float mean_wait, 
					int max_length, int actual_length, int total_length, float mean_length,
					int last_link, int number){
		this.queue = queue;
		this.max_wait = max_wait;
		this.total_wait = total_wait;
		this.mean_wait = mean_wait;
		this.max_lenght = max_length;
		this.actual_length = actual_length;
		this.total_length = total_length;
		this.mean_length = mean_length;
		this.last_link = last_link;
		this.number = number;
	}
	
	/* this method is always called when a new link is inserted in the queue */
	public void updateListHeader(Queue queue, int new_wait, int actual_length, 
			int new_length, int last_link, boolean increase){
		this.queue = queue;
		this.total_wait = this.total_wait + new_wait;
		this.actual_length = actual_length;
		this.total_length = this.total_length + new_length;
		this.last_link = last_link;
		if(increase) this.number++;
	}
	
	public Queue get_queue(){
		return queue;
	}
	public void set_queue(Queue queue){
		this.queue = queue;
	}
	
	public int get_actual_length(){
		return actual_length;
	}
	
	public int get_last_time(){
		return last_link;
	}
	
	public void displayStatus(int TIME){
		this.mean_wait = (float) this.total_wait/this.number;
		this.mean_length = (float) this.total_length/TIME;
		
		System.out.println("	Total wait: " + this.total_wait);
		System.out.println("	Mean wait: " + this.mean_wait);
		System.out.println("	Total lenght: " + this.total_length);
		System.out.println("	Mean lenght: " + this.mean_length);
		System.out.println("	Total number of jobs: " + this.number);
	}
}
