package System;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import Cells.Queue;
import Cells.emptyCM;
import Cells.eventTable;
import Cells.jobCM;
import Cells.jobTable;
import LinkedLists.EventLinkedList;
import LinkedLists.JobLinkedList;
import System.Peripherals.Disk;
import System.Peripherals.Printer;
import System.Peripherals.Reader;

public class Simulator {
	
	int TIME;	//time of the simulation, initializes with zero.
	
	CPU cpu;						//Central processing unit of the system
	CentralMemory centralMemory;	//Central memory of the system
	Disk disk;
	Reader reader;
	Printer printer;
	
	JobLinkedList jobLinkedList;	//List of jobs in the simulation
	EventLinkedList eventLinkedList;//List of events for each job in the system 
	BufferedReader jobsParameters;	//Gets parameters for all jobs from a text file
	BufferedReader systemParameters;//Gets parameters for all parts of system from a text file
	String line;					//Contains parameters for one job
	
	/* Constructor of the Simulator class */
	public Simulator() throws IOException{
		TIME = 0;	//Initializes TIME with zero
		jobsParameters = new BufferedReader(new FileReader(new File("jobTable.txt")));
		
		cpu = new CPU();
		centralMemory = new CentralMemory(20000, 10);	//Initializing CM with its size
		disk = new Disk();
		reader = new Reader();
		printer = new Printer();
		
		jobLinkedList = new JobLinkedList();
		eventLinkedList = new EventLinkedList();
		
		initializeSystem();
	}
	
	/* Returns if there are no more job parameters in the list of jobs in the system */
	public boolean endOfJobs(){
		return jobLinkedList.isEmpty();
	}
	
	/* Returns the job linked list */
	public JobLinkedList getJobLinkedList(){
		return jobLinkedList;
	}
	
	/* Method creates object job of jobTable class with attributes passed by text file */ 
	void createJob(String line){
		String[] splited = line.split("\\s+");
		
		jobTable job;
		
		/* The order of the inputs must be respected in the input file */
		try{
			int jobID = Integer.parseInt(splited[0]);
			int arrival_time = Integer.parseInt(splited[1]);
			int job_space = Integer.parseInt(splited[2]);
			int total_time = Integer.parseInt(splited[3]);
			int number_requests = Integer.parseInt(splited[4]);
			
			int[] peripherals = new int[number_requests];
			int i = 0;
			while(i < number_requests){
				 peripherals[i] = Integer.parseInt(splited[5 + i]);
				 i++;
			}
			
			int[] record_lenghts = new int[number_requests];
			int j = 0;
			while(j < number_requests){
				 record_lenghts[j] = Integer.parseInt(splited[5 + i + j]);
				 j++;
			}
			
			int priority = Integer.parseInt(splited[5 + i + j]);
			
			/* create job with attributes passed in the input text file 
			 * values for flag and time remaining are, by default, 
			 * initialized in false and zero.*/
			job = new jobTable(jobID, arrival_time, job_space, total_time, 
					number_requests, peripherals, record_lenghts, false, total_time/number_requests, priority);
			
			
			jobLinkedList.insertLink(job);
			
		} catch (NumberFormatException e) {
			System.err.println(e);
		}
	}
	
	/* This method creates the event object for a job */
	void createEventForJob(jobTable job){
		int event_time = job.get_arrival_time(); //event 1 is scheduled to occur at the time job arrives
		eventTable event = new eventTable(1, event_time, job); //creates event cell
		
		eventLinkedList.insertLink(event); //insert new cell into the list
	}
	
	/* This method initializes system by reading first job parameters */
	public void initializeSystem() throws IOException{
		line = jobsParameters.readLine();
		if(line != null){ //if line contains job parameters, continue
			createJob(line); //creates job cell for the first job and add it into the linked list
			createEventForJob(jobLinkedList.getFirstJob()); //creates event 1 for the first job
			
			System.out.println("Job " + jobLinkedList.getFirstJob().get_job_id() + " arrived system.");
			
			jobLinkedList.getFirstJob().retrieveData();
		}
	}
	
	/* Gets next event (with the lesser event_time) in the eventLinkedList 
	 * and execute method according to its event identifier */
	public void selectEvent() throws IOException{
		System.out.println("\n----------------------------------------------------------------------------------------");
		eventTable nextEvent = eventLinkedList.getNextEvent();
		if(nextEvent != null){
			int event_identifier = nextEvent.get_event_identifier();
			
			/* set time of the simulation at time of execution of the next event */
			if(TIME <= nextEvent.get_event_time())
				TIME = nextEvent.get_event_time();
			else
				nextEvent.set_event_time(TIME);
			
			
			System.out.println("\nTime now: " + TIME + " | Executed event: " + event_identifier + " | Job ID: " + nextEvent.get_event_job().get_job_id());
			executeEvent(event_identifier, nextEvent);
			eventLinkedList.displayStatus();	//display status of the event list
		}
	}
	
	/* This method calls a method according to the event identifier */
	void executeEvent(int event_identifier, eventTable nextEvent) throws IOException{
		switch (event_identifier){
		case 1:
			event1(nextEvent);
			break;
		case 2:
			event2(nextEvent);
			break;
		case 3:
			event3(nextEvent);
			break;
		case 4:
			event4(nextEvent);
			break;
		case 5:
			event5(nextEvent);
			break;
		case 6:
			event6(nextEvent);
			break;
		case 7:
			event7(nextEvent);
			break;
		default:
			System.out.println("Invalid event");
		}
	}
	
	/* This event sets next event_identifier = 2 for the job and adds next job into event list*/ 
	void event1(eventTable event) throws IOException{
		event.set_event_identifier(2);
		event.set_event_time(TIME);
		/* gets next line of job and add it into the system*/
		line = jobsParameters.readLine();
		if(line != null){ 		//if line contains job parameters, continue
			createJob(line); 	//creates job cell for the first job and add it into the linked list
			createEventForJob(jobLinkedList.getFirstJob()); //creates event 1 for the first job and add it into the event linked list
			
			System.out.println("Job " + jobLinkedList.getFirstJob().get_job_id() + " arrived system");
			
			jobLinkedList.getFirstJob().retrieveData();	 //prints data
		} else {
			System.out.println("No more jobs to be added in the system");
		}
	}
	
	/* This method allocates memory for job, if there is sufficient, otherwise put it in the queue */
	void event2(eventTable event) throws IOException{
		if(centralMemory.allocateJob(event.get_event_job())){ //if there is enough space to allocate
			event.set_event_identifier(3);
			event.set_event_time(TIME + centralMemory.get_relocation_time());
			
			System.out.println("Job " + event.get_event_job().get_job_id() + " successfully allocated in the CM");
			
		} else {	//there is not enough space for the job
			if(!event.get_in_queue()){	//if event is not in a queue
				jobTable job = event.get_event_job();
				
				//creates cell in the queue list
				Queue queue = new Queue(job, job.get_priority(), TIME);
	
				//computes overhead time
				event.set_event_time(TIME + centralMemory.get_relocation_time());
				
				//insert new cell in the queue list
				centralMemory.cmQueueLinkedList.insertLink(queue);
				event.set_in_queue(true);
				System.out.println("Not enough space for allocation. Job " + job.get_job_id() + " added into CM queue");
			}
		}
		
		centralMemory.displayStatus();
	}
	
	/* this method executes job in the cpu */
	void event3(eventTable event){
		if(!cpu.get_occupied()){	//if cpu is available, continue
			jobTable job = event.get_event_job();
			cpu.executeJob(job);	//become occupied
			
			if(job.get_record_count() > 0){	//still have time to be processed
				job.decrement_record_count();
				event.set_event_identifier(4);
				if(job.get_flag()){ //if job was interrupted before, set time remaining
					//set flag in false again
					job.set_flag(false);
					event.set_event_time(TIME + job.get_time_remaining());
				} else {	//if job wasn't interrupted before, set time interrequest
					event.set_event_time(TIME + job.get_request_time());
				}
				System.out.println("Job " + job.get_job_id() + " being executed by CPU");
			} else {	//all time to be processed have already passed
				event.set_event_identifier(7);
				event.set_event_time(TIME);
				System.out.println("Job " + job.get_job_id() + " already executed in the CPU");
			}
		} else {	//if it is occupied...
			if(cpu.get_job_being_executed() == event.get_event_job()){ //if is occupied executing this job, continues
				if(event.get_event_job().get_record_count() > 0){	//still have time to be processed
					event.get_event_job().decrement_record_count();
					event.set_event_identifier(4);
					if(event.get_event_job().get_flag()){ //if job was interrupted before, set time remaining
						//set flag in false again
						event.get_event_job().set_flag(false);
						event.set_event_time(TIME + event.get_event_job().get_time_remaining());
					} else {	//if job wasn't interrupted before, set time interrequest
						event.set_event_time(TIME + event.get_event_job().get_request_time());
					}
					System.out.println("Job " + event.get_event_job().get_job_id() + " being executed by CPU");
				} else {	//all time to be processed have already passed
					event.set_event_identifier(7);
					event.set_event_time(TIME);
					System.out.println("Job " + event.get_event_job().get_job_id() + " already executed in the CPU");
				}
			} 
			//if is executing another job and the arrived job has lower priority, put this one in the CPU queue
			else if(cpu.get_job_being_executed().get_priority() >= event.get_event_job().get_priority()){	
				if(!event.get_in_queue()){	//if event is not in a queue	
					jobTable job = event.get_event_job();
					System.out.println("CPU is busy, job" + job.get_job_id() + ", with lower priority added into CPU queue");
					Queue cpuQueue = new Queue(job, job.get_priority(), TIME);
					cpu.cpuQueueLinkedList.insertLink(cpuQueue);	//insert job in the queue
					event.set_in_queue(true);	//event is in the queue
				}
			}
			//if is executing another job but the arrived job has higher priority, allocate this one in CPU
			else if(cpu.get_job_being_executed().get_priority() < event.get_event_job().get_priority()){
				
				System.out.println("CPU is busy, but job " + event.get_event_job().get_job_id() + " has higher priority");
				System.out.println("Job " + event.get_event_job().get_job_id() + " allocated to CPU and " +
						"job " + cpu.get_job_being_executed().get_job_id() + " interrupted to continue later");
				
				//time to finish execution of job in the CPU
				int final_time = eventLinkedList.getEventFromJob(cpu.job_being_executed).get_event_time();
				//set time to finish job execution in the job
				cpu.get_job_being_executed().set_time_remaining(TIME - final_time);
				//set flag of interruption in true
				cpu.get_job_being_executed().set_flag(true);
				//return event for the job in cpu to 3 again
				eventLinkedList.getEventFromJob(cpu.get_job_being_executed()).set_event_identifier(3);
				//finish job of cpu
				cpu.finishJob();
				
				//now, allocates job with higher priority in cpu
				jobTable job = event.get_event_job();
				cpu.executeJob(job);	//become occupied
				job.decrement_record_count();
				event.set_event_identifier(4);
				if(job.get_flag()){ //if job was interrupted before, set time remaining
					//set flag of interruption in false
					job.set_flag(false);
					event.set_event_time(TIME + job.get_time_remaining());
				} else {	//if job wasn't interrupted before, set time interrequest
					event.set_event_time(TIME + job.get_request_time());
				}
			}
		}
	}
	
	/* release cpu, execute next in the queue and go to next event*/
	void event4(eventTable event){
		cpu.finishJob();
		event.set_event_identifier(5);
		event.set_event_time(TIME + cpu.get_overhead_time());
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " released CPU to execute I/O process");
		
		Queue jobsInCPUQueue = cpu.cpuQueueLinkedList.getFirstLink();
		if(jobsInCPUQueue != null){
			jobTable job = jobsInCPUQueue.get_job();
			
			cpu.cpuQueueLinkedList.removeLink(job, TIME);	//removed from queue
			cpu.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			eventLinkedList.getEventFromJob(job).set_event_time(TIME);//will execute now
			
			System.out.println("Job " + job.get_job_id() + " waiting in the CPU queue was successfully allocated in the CPU");
		}
	}
	
	/* this method select which peripheral will be used for I/O */
	void event5(eventTable event){
		int peripheral = event.get_event_job().get_record_count(); //the position of the peripheral in the array at this time
		switch(event.get_event_job().get_peripherals()[peripheral]){//gets the code of the peripheral used this time
			case 0:
				event5forDisk(event);
				break;
			case 1:
				event5forReader(event);
				break;
			case 2:
				event5forPrinter(event);
				break;
		}
	}
	
	void event5forDisk(eventTable event){
		if(disk.get_occupied()){ //if disk is already being used
			if(disk.get_job() == event.get_event_job()){ //if is occupied executing this job, continues
				
				event.set_event_identifier(6);
				event.set_event_time(TIME + disk.get_job().get_record_lengths()[disk.get_job().get_record_count()]);
				
				System.out.println("Job " + event.get_event_job().get_job_id() + " started Disk I/O");
			} else {	//if is executing another job, add this one to the queue
				if(!event.get_in_queue()){	//if event is not in a queue	
					jobTable job = event.get_event_job();
					Queue queue = new Queue(job, job.get_priority(), TIME);
					disk.queueLinkedList.insertLink(queue);	//insert job in the queue
					event.set_in_queue(true);	//event is in the queue
					System.out.println("Disk is busy, job" + job.get_job_id() + " is being added into the queue");
				}
			}
		} else {
			jobTable job = event.get_event_job();
			disk.executeJob(job);
			event.set_event_identifier(6);
			event.set_event_time(TIME + job.get_record_lengths()[job.get_record_count()]);
			
			System.out.println("Disk allocated to the job " + job.get_job_id());
		}
	}
	
	void event5forReader(eventTable event){
		if(reader.get_occupied()){ //if reader is already being used
			if(reader.get_job() == event.get_event_job()){ //if is occupied executing this job, continues
				
				event.set_event_identifier(6);
				event.set_event_time(TIME + reader.get_job().get_record_lengths()[reader.get_job().get_record_count()]);
				
				System.out.println("Job " + event.get_event_job().get_job_id() + " started Reader I/O");
			} else {	//if is executing another job, add this one to the queue
				if(!event.get_in_queue()){	//if event is not in a queue	
					jobTable job = event.get_event_job();
					Queue queue = new Queue(job, job.get_priority(), TIME);
					reader.queueLinkedList.insertLink(queue);	//insert job in the queue
					event.set_in_queue(true);	//event is in the queue
					System.out.println("Reader is busy, job" + job.get_job_id() + " is being added into the queue");
				}
			}
		} else {
			jobTable job = event.get_event_job();
			reader.executeJob(job);
			event.set_event_identifier(6);
			event.set_event_time(TIME + job.get_record_lengths()[job.get_record_count()]);
			
			System.out.println("Reader allocated to the job " + job.get_job_id());
		}
	}

	void event5forPrinter(eventTable event){
		if(printer.get_occupied()){ //if reader is already being used
			if(printer.get_job() == event.get_event_job()){ //if is occupied executing this job, continues
				
				event.set_event_identifier(6);
				event.set_event_time(TIME + printer.get_job().get_record_lengths()[printer.get_job().get_record_count()]);
				
				System.out.println("Job " + event.get_event_job().get_job_id() + " started Reader I/O");
			} else {	//if is executing another job, add this one to the queue
				if(!event.get_in_queue()){	//if event is not in a queue	
					jobTable job = event.get_event_job();
					Queue queue = new Queue(job, job.get_priority(), TIME);
					printer.queueLinkedList.insertLink(queue);	//insert job in the queue
					event.set_in_queue(true);	//event is in the queue
					System.out.println("Printer is busy, job" + job.get_job_id() + " is being added into the queue");
				}
			}
		} else {
			jobTable job = event.get_event_job();
			printer.executeJob(job);
			event.set_event_identifier(6);
			event.set_event_time(TIME + job.get_record_lengths()[job.get_record_count()]);
			
			System.out.println("Printer allocated to the job " + job.get_job_id());
		}
	}
	
	/* this method releases I/O peripheral from the  job */
	void event6(eventTable event){
		int peripheral = event.get_event_job().get_record_count(); //the position of the peripheral in the array at this time
		switch(event.get_event_job().get_peripherals()[peripheral]){//gets the code of the peripheral used this time
			case 0:
				event6forDisk(event);
				break;
			case 1:
				event6forReader(event);
				break;
			case 2:
				event6forPrinter(event);
				break;
		}
	}
	
	void event6forDisk(eventTable event){
		disk.finishJob();
		
		//now, check if there is a job in the queue waiting for being allocated
		Queue jobsInQueue = disk.queueLinkedList.getFirstLink();
		if(jobsInQueue != null){
			jobTable job = jobsInQueue.get_job();
			
			disk.queueLinkedList.removeLink(job, TIME);	//removed from queue
			disk.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Job " + job.get_job_id() + " waiting in the Disk queue was successfully allocated in the Disk");
		}
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " released Disk for returning to CPU");
		
		event.set_event_identifier(3);
		event.set_event_time(TIME + disk.get_overhead_time());
	}
	void event6forReader(eventTable event){
		reader.finishJob();
		
		//now, check if there is a job in the queue waiting for being allocated
		Queue jobsInQueue = reader.queueLinkedList.getFirstLink();
		if(jobsInQueue != null){
			jobTable job = jobsInQueue.get_job();
			
			reader.queueLinkedList.removeLink(job, TIME);	//removed from queue
			reader.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Job " + job.get_job_id() + " waiting in the Reader queue was successfully allocated in the Reader");
		}
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " released Reader for returning to CPU");
		
		event.set_event_identifier(3);
		event.set_event_time(TIME + reader.get_overhead_time());
	}
	void event6forPrinter(eventTable event){
		printer.finishJob();
		
		//now, check if there is a job in the queue waiting for being allocated
		Queue jobsInQueue = printer.queueLinkedList.getFirstLink();
		if(jobsInQueue != null){
			jobTable job = jobsInQueue.get_job();
			
			printer.queueLinkedList.removeLink(job, TIME);	//removed from queue
			printer.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Job " + job.get_job_id() + " waiting in the Printer queue was successfully allocated in the Printer");
		}
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " released Printer for returning to CPU");
		
		event.set_event_identifier(3);
		event.set_event_time(TIME + printer.get_overhead_time());
	}
	
	void event7(eventTable event){
		//----------remove job from the system----------//
		jobLinkedList.removeJob(event.get_event_job());
		eventLinkedList.removeEvent(event);
		
		//----------remove job from the central memory----------//
		jobCM removedJob = centralMemory.cmJobLinkedList.removeJobCM(event.get_event_job());
		/* transform the space, before occupied by a job, in a free space */
		emptyCM restoredSpace = new emptyCM(removedJob.get_initial_address(), removedJob.get_final_address());
		centralMemory.cmEmptyLinkedList.restoreEmptySpace(restoredSpace);
		
		System.out.println("Job " + event.get_event_job().get_job_id() + " finished execution in the system. " +
				"It released CPU and CM.\n");
		
		/* now, it is necessary to look in the CM queue, to check if there is a job to be allocated */
		Queue jobsInQueue = centralMemory.cmQueueLinkedList.getFirstLink();
		while(jobsInQueue != null){
			if(centralMemory.allocateJob(jobsInQueue.get_job())){ //if there is enough space to allocate
				eventLinkedList.getEventFromJob(jobsInQueue.get_job()).set_event_identifier(3);
				eventLinkedList.getEventFromJob(jobsInQueue.get_job()).set_event_time(TIME + centralMemory.get_relocation_time());
				centralMemory.cmQueueLinkedList.removeLink(jobsInQueue.get_job(), TIME);
				eventLinkedList.getEventFromJob(jobsInQueue.get_job()).set_in_queue(false); //not in the queue anymore
				
				System.out.println("Job " + jobsInQueue.get_job().get_job_id() + " waiting in the CM queue was successfully allocated in the CM");
			}
			jobsInQueue = jobsInQueue.nextLink;
		}
		
		centralMemory.displayStatus();
		
		//----------remove job from the CPU----------//
		cpu.finishJob();
		//now, check if there is a job in the cpu queue waiting for being executed
		Queue jobsInCPUQueue = cpu.cpuQueueLinkedList.getFirstLink();
		if(jobsInCPUQueue != null){
			jobTable job = jobsInCPUQueue.get_job();
			
			cpu.cpuQueueLinkedList.removeLink(job, TIME);	//removed from queue
			cpu.executeJob(job);
			eventLinkedList.getEventFromJob(job).set_in_queue(false); //not in a queue anymore
			
			System.out.println("Job " + job.get_job_id() + " waiting in the CPU queue was successfully allocated in the CPU");
		}
		
	}
	
	
	public void printEstatistics(){
		System.out.println("\nEnd of simulation at TIME = " + TIME);
		System.out.println("Central memory queue estatistics -> ");
		centralMemory.cmQueueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nCPU queue estatistics -> ");
		cpu.cpuQueueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nDisk queue estatistics -> ");
		disk.queueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nReader queue estatistics -> ");
		reader.queueLinkedList.getListHeader().displayStatus(TIME);
		
		System.out.println("\nPrinter queue estatistics -> ");
		printer.queueLinkedList.getListHeader().displayStatus(TIME);
	}
}
