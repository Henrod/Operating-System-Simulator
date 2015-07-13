package System.Peripherals;

import Cells.jobTable;
import LinkedLists.QueueLinkedList;

public class Printer extends Peripheral {

	public Printer(int overhead_time, int max_wait_queue, int max_length_queue){
		this.occupied = false;
		this.job_being_executed = null;
		this.queueLinkedList = new QueueLinkedList(max_wait_queue, max_length_queue);
		this.overhead_time = overhead_time;
	}
	
	@Override
	public boolean get_occupied() {
		return this.occupied;
	}

	@Override
	public void executeJob(jobTable job) {
		this.occupied = true;
		this.job_being_executed = job;
	}
	
	@Override
	public void finishJob() {
		this.job_being_executed = null;
		this.occupied = false;
	}

	@Override
	public jobTable get_job() {
		return this.job_being_executed;
	}
	
	@Override
	public int get_overhead_time() {
		return this.overhead_time;
	}
}
