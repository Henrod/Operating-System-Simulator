package System.Peripherals;

import Cells.jobTable;
import LinkedLists.QueueLinkedList;

public class Printer extends Peripheral {

	public Printer(){
		this.occupied = false;
		this.job_being_executed = null;
		this.queueLinkedList = new QueueLinkedList(1000, 5);
		this.overhead_time = 20;
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
